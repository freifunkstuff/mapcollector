# Mapcollector - Kartenaggregator für Freifunk auf Basis der Freifunk-Dresden Firmware

## Funktionsweise

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
