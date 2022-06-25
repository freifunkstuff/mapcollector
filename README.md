# Mapcollector - Kartenaggregator für Freifunk auf Basis der Freifunk-Dresden Firmware

## Funktionsweise Kartenzugriff

Benötigt wird ein [Gateway](https://freifunk-dresden.github.io/ffdd-server/) oder ein Node, welcher mit dem Freifunk-Dresden Netz verbunden ist. Dieser zeigt unter nodes.cgi _alle_ Konten des verbundenen Netzes an.

Zusätzlich muss eine Proxy-Konfiguration hinterlegt sein, die es ermöglicht, Knoten über das Gateway oder den Node abzufragen. Alternativ kann auch ein Routing eingerichtet werden, welches dem Mapcollector direkten Zugriff auf Nodes erlaubt.

Für ein Gateway kann für den Proxy die Datei `/etc/apache2/sites-enabled/additional_80.conf` angelegt werden:

```
ProxyRequests On
ProxyVia On

<Proxy *>
    Order deny,allow
    Deny from all
</Proxy>

<ProxyMatch "^http://10\.200\.\d{1,3}\.\d{1,3}/sysinfo-json\.cgi$">
    Order deny,allow
    Allow from all
    AuthType Basic
    AuthName "Password Required"
    AuthUserFile /etc/config/proxy_auth
    Require valid-user
</ProxyMatch>
```

Zusätzlich muss ein User mit `htpasswd -c /etc/config/proxy_auth proxyusername` für den Zugriff auf den Proxy erzeugt werden.


## Arbeitsweise

### Datenhaltung

* KeyDB (Redis-kompatibler Key-Value-Store) für die Knoten-Infos (Redis kann auch verwendet werden)
* VictoriaMetrics (Prometheuts-kompatible Zeitreihendatenbank) für statistische Daten (Pometheus kann auch verwendet werden)

### Nodeliste aktualisieren

* Knoten werden zyklisch von der Übersichtsseite des Gateways abgerufen
* Neue Knoten werden mit ID und Adresse in die Nodeliste eingetragen
   * "firstSeen" wird gesetzt
* Für bestehende Knoten wird "lastSeen" aktualisiert
   
### Nodes aktualisieren

* Alle Knoten werden zyklisch durchlaufen
* Treffen bestimmte Bedingungen zu, wird über den Proxy eine direkte Verbindung zum Knoten aufgebaut und /sysinfo-json.cgi abgerufen
    * Knoten, die noch nie abgefragt wurden, werden sofort abgefragt
    * Knoten, deren Community nicht bekannt ist, werden jede Minute abgefragt
    * Knoten, die seit weniger als 2 Tagen bekannt sind, werden jede Minute abgefragt
    * Alle anderen Knoten werden 1x pro 5 Minuten abgefragt
    * wenn ein Community-Filter aktiv ist
	    * Knoten der eigenen Community werden 1x pro Minute abgefragt
	    * Knoten anderer Communities werden 1x pro 15 Minuten abgefragt (Community könnte sich ja ändern)
	    * statistische Daten werden nur für Knoten der eigenen Community erhoben
* Datum/Uhrzeit des letzen Abrufs wird gespeichert
* ist der Knoten erreichbar
	* lastSeen wird aktualisiert
	* Metadaten des Knotens werden aktualisiert
	* Statistik des Knontens wird aktualisiert und an die Zeitreihen-Datenbank geschickt
* ist der Knoten nicht erreichbar
    * Online-Status des Knotens wird ermittelt (offline = X Minuten nach lastSeen)
    * Online-Status wird an Zeitreihen-Datenbank geschickt


### Nodes löschen

* Knoten mit einer Node-ID < 1000 werden gelöscht, wenn sie >24h nicht online waren
* Knoten werden aus der Datenbank gelöscht, wenn sie >30 Tage nicht online waren
* Für alle Knoten wird eine interne ID für die Statistik vergeben. Damit bekommt ein Node, der gelöscht und später neu angelegt wurde, eine neue Statistik


### Dynamisches Timeout (Idee, nicht umgesetzt)

* die Dauer eines Requests wird gespeichert
* gab es einen Timeout beim letzten Request, wird der Timeout beim nächsten Request erhöht
* das Abfrageintervall wird bei höherem Timeout ebenfalls vergrößert
* eine Statistik über die letzten N Aufrufe wird mitgeführt. Ist ein Knoten längere Zeit wieder mit kurzem Timeout erreichbar werden Timeout und Abfrageintervall wieder auf Standard gesetzt.


### Traffic-Überlegungen

* Abruf eines Knotens: ~6 kByte
* Abruf eines Knotens alle 5 Minuten: ~50 MByte/Monat
* Abruf von 800 Knoten alle 5 Minuten: ~40 GByte/Monat
* Abruf eines Knotens jede Minute: ~250 MByte/Monat
* Abruf von 800 Knoten jede Minute: ~200 GByte/Monat

### Node-Stammdaten

Stammdaten behalten ihre Gültigkeit, wenn der Node temporär offline ist. Die folgenden Stammdaten werden von Knoten abgefragt

* firmware version
* model
* node_type
* autoupdate
* node name
* community
* ip
* location lat/lon/alt
* email
* note

### Node-Statistiken

Statistiken haben nur Gültigkeit, solange der Node online ist. Die folgenden Statistiken werden von Knoten abgefragt:

* uptime
* clients2g (5min)
* clients5g (5min)
* traffic-statistiken (Node)
    * wifi2_rx/tx
    * wifi5_rx/tx
* traffic-statistiken (Server)
    * traffic_wan rx/tx
    * traffic_tbb_fastd tx/tx
    * traffic_ovpn tx/tx
* memory free
* cpu load (5min)
* prefered gateway
* selected gateway
* airtime 2g
* airtime 5g
* network-switch
    * port-name + speed


### Node-Links

Links werden immer an beiden Seiten erfasst. Es kann aber sein, dass eine der beiden Seiten nicht erreichbar ist, dann kann der Link nur von einer Seite her erfasst werden.

* Link-ID: besteht aus 2 Knoten-Nummern und dem Type. Die kleinere Knotennummer steht immer vorne.
* Knoten beider Seiten
* Mesh-Type (lan, wifi_mesh)
* rq auf Seite des aktuellen Knotens als Qualitätsindex
* tq auf Seite des aktuellen Knotens als Qualitätsindex der anderen Richtung als Fallback, wenn die Statistik des anderen Knotens fehlt

