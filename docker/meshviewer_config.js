Object.assign(window.config,
  {
    'nodeInfos': [
      {
        'name': 'Clientstatistik',
        'href': '/grafana/d/KoKOqJc7k/node-public?orgId=1&refresh=30s&from=now-24h&to=now&var-node_id={NODE_ID}',
        'image': '/render/clients/{NODE_ID}',
        'title': 'Entwicklung der Anzahl der Clients'
      },
      {
        'name': 'Traffic',
        'href': '/grafana/d/KoKOqJc7k/node-public?orgId=1&refresh=30s&from=now-24h&to=now&var-node_id={NODE_ID}',
        'image': '/render/traffic/{NODE_ID}',
        'title': 'Traffic'
      }
    ],
    // Array of data provider are supported
    'dataPath': [
      'http://192.168.37.131:8080/meshviewer/'
    ],
    'hwImg': 'https://freifunkstuff.github.io/meshviewer-hwimages/hwimg/{MODEL_NORMALIZED}.svg?{MODEL_HASH}',
    'siteName': 'Freifunk Leipzig',
    'mapLayers': [
      {
        "name": "CartoDB",
        "url": "https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png",
        "config": {
          "maxZoom": 22,
          "attribution": "&copy; <a href=\"http://www.openstreetmap.org/copyright\">OpenStreetMap</a>, &copy; | <a href=\"https://carto.com/attribution\">CARTO</a>"
        }
      },
      {
        'name': 'OpenStreetMap.HOT',
        'url': 'https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png',
        'config': {
          'maxZoom': 20,
          'attribution': '&copy; Openstreetmap France | &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }
      },
      {
        'name': 'Stamen Toner',
        'url': 'http://stamen-tiles-c.a.ssl.fastly.net/toner/{z}/{x}/{y}.png',
        'config': {
          'maxZoom': 20,
          'attribution': 'https://stamen.com/open-source/'
        }
      },
      {
        'name': 'Esri.WorldImagery',
        'url': '//server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
        'config': {
          'maxZoom': 20,
          'attribution': 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'
        }
      }
    ],
    // Set a visible frame
    'fixedCenter': [
      // Northwest
      [
  	51.4168,
        12.1983
      ],
      // Southeast
      [
	51.2516,
	12.4791
      ]
    ],
    'domainNames': [
      {
        'domain': 'l',
        'name': 'Leipzig (Gluon)'
      },
      {
        'domain': 'meshkit',
        'name': 'Leipzig (meshkit)'
      }
    ],
    'linkList': [
      {
        'title': 'Impressum',
        'href': 'https://freifunk.net/impressum/'
      },
      {
        'title': 'Datenschutz',
        'href': 'https://freifunk.net/datenschutz/'
      }
    ]
  });
