# Derulare fonduri europene

## How to run
1. Install Play activator ( https://www.playframework.com/documentation/2.5.x/Installing )
2. cd to project location
3. $activator run -J-Xmx4G

## How to deploy
1. Using activator update production.conf with the new secret generated with [eu-funds] $ playGenerateSecret
2. run [eu-funds] $ dist
3. distribute the generated zip file
4. unzip
5. chmod +x /path/to/yourapp/bin/eu-funds
6. run with sh /path/to/yourapp/bin/eu-funds -Dconfig.file=conf/production.conf 

## Endpoints
1. 
Root
> GET     /     

2. 
Refresh data
> GET     /refresh

3. 
Search for data
> GET     /list?filter=???&from=0&size=25


## Config

Edit /conf/application.conf
- gov.url = "http://data.gov.ro/dataset/informatii-derulare-fonduri-europene-smis"
- elastic.data.path = "data"

## Examples
### How many funds accessed in an area:
- /list?filter=Constanta&from=0&size=200
```javascript
"hits" : { "total" : 61527, ...
```

### Field querying:
- /list?filter=TITLU_PROIECT:Constructie autostrada&from=0&size=100
```javascript
RichSearchResponse({
  "took" : 11,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 4398,
    "max_score" : 3.4092171,
    "hits" : [ {
      "_index" : "gov",
      "_type" : "funds",
      "_id" : "AVfB76SJcouILs8nc8Rb",
      "_score" : 3.4092171,
      "_source" : {
        "COD_OPER" : "***",
        "NUMAR_AUTORIZARE" : "8.0",
        "REGIUNEA_BENEFICIARULUI" : "BUCURESTI - ILFOV",
        "COD_PO" : "5",
        "TITLU_PROIECT" : "Constructie autostrada Cernavoda-Constanta",
        "JUDETUL_BENEFICIARULUI" : "BUCURESTI",
        "NUME_BENEFICIAR" : "COMPANIA NATIONALA DE AUTOSTRAZI SI DRUMURI NATIONALE DIN ROMANIA",
        "MOTIV_INTIRZIERE" : "",
        "DATA_DEPUNERE_CERERE" : "24-SEP-12",
        "CHEIE" : "3460\\8",
        "COD_SMIS" : "3460.0",
        "REIMBURS_OP_LIST" : "",
        "COD_BENEFICIAR" : "279.0",
        "VALOARE_AUTORIZATA" : "1388.61",
        "DATA_AUTORIZATA" : "28-AUG-13",
        "COD_DMI" : "1.1",
        "VALOARE_ELIGIBILA_CERUTA" : "1388.61",
        "VALOARE_RAMBURSATA" : "",
        "LOCALITATEA_BENEFICIARULUI" : "MUNICIPIUL BUCURESTI",
        "DATA_ULTIMEI_PLATI" : "",
        "COD_AP" : "1"
      }
    }, {
      "_index" : "gov",
      "_type" : "funds",
      "_id" : "AVfB758kcouILs8nc7Vz",
      "_score" : 3.4092171,
      "_source" : {
        "COD_OPER" : "***",
        "NUMAR_AUTORIZARE" : "22.0",
        "REGIUNEA_BENEFICIARULUI" : "BUCURESTI - ILFOV",
        "COD_PO" : "5",
        "TITLU_PROIECT" : "Constructie autostrada Cernavoda-Constanta",
        "JUDETUL_BENEFICIARULUI" : "BUCURESTI",
        "NUME_BENEFICIAR" : "COMPANIA NATIONALA DE AUTOSTRAZI SI DRUMURI NATIONALE DIN ROMANIA",
        "MOTIV_INTIRZIERE" : "",
        "DATA_DEPUNERE_CERERE" : "04-NOV-14",
        "CHEIE" : "3460\\22",
        "COD_SMIS" : "3460.0",
        "REIMBURS_OP_LIST" : "",
        "COD_BENEFICIAR" : "279.0",
        "VALOARE_AUTORIZATA" : "0.0",
        "DATA_AUTORIZATA" : "17-DEC-15",
        "COD_DMI" : "1.1",
        "VALOARE_ELIGIBILA_CERUTA" : "2.12307753E7",
        "VALOARE_RAMBURSATA" : "",
        "LOCALITATEA_BENEFICIARULUI" : "MUNICIPIUL BUCURESTI",
        "DATA_ULTIMEI_PLATI" : "",
        "COD_AP" : "1"
      }
    }, {
    ...
```
