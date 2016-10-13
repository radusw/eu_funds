# Derulare fonduri europene

## How to run
1. Install Play activator ( https://www.playframework.com/documentation/2.5.x/Installing )
2. $activator run -J-Xmx4G

## Endpoints
1. 
Root
GET     /   

2. 
Search for data (limited to 10 results)
GET     /list?filter=<String>      

3. 
Refresh data
GET     /refresh


## Config

Edit /conf/application.conf
- gov.url = "http://data.gov.ro/dataset/informatii-derulare-fonduri-europene-smis"
- elastic.data.path = "data"

## Example
/list?filter=%22MIERCUREA%22
```RichSearchResponse({
  "took" : 11,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "failed" : 0
  },
  "hits" : {
    "total" : 4774,
    "max_score" : 1.5697464,
    "hits" : [ {
      "_index" : "gov",
      "_type" : "funds",
      "_id" : "AVe-E19PtGSdOGULUHzh",
      "_score" : 1.5697464,
      "_source" : {
        "Titluproiuect" : "Noi sanse pentru Angajare",
        "CodOPER" : "MUNICIPIUL MIERCUREA CIUC",
        "Dataautorizata" : "41759.0",
        "Motivintarziere" : "5.0",
        "Cheie" : "29516\\7",
        "CodPO" : "***",
        "Codbeneficiar" : "5.1",
        "Valoarerambursata" : "4.0",
        "CodDMI" : "HARGHITA",
        "CodSMIS" : "29516.0",
        "Dataultimeiplati" : "5183.0",
        "CodAP" : "CENTRU",
        "NrAutorizare" : "7.0",
        "Valoareeligibilaceruta" : "6724.3",
        "Numebeneficiar" : "ASOCIATIA PENTRU TINERET FIDELITAS MIERCUREA-CIUC",
        "Valoareautorizata" : "6724.3",
        "Datadepunerecerere" : "41743.0"
      }
    }, {
      "_index" : "gov",
      "_type" : "funds",
      "_id" : "AVe-E3D-tGSdOGULUQJq",
      "_score" : 1.5697464,
      "_source" : {
        "Titluproiuect" : "Fii stapanul tau",
        "Dataautorizata" : "4.0",
        "Motivintarziere" : "CENTRU",
        "Cheie" : "12503\\7",
        "CodPO" : "MUNICIPIUL MIERCUREA CIUC",
        "Codbeneficiar" : "HARGHITA",
        "Valoarerambursata" : "***",
        "CodSMIS" : "12503.0",
        "Dataultimeiplati" : "3.1",
        "NrAutorizare" : "7.0",
        "Valoareeligibilaceruta" : "5183.0",
        "Numebeneficiar" : "ASOCIATIA PENTRU TINERET FIDELITAS MIERCUREA-CIUC",
        "Valoareautorizata" : "3.0",
        "Datadepunerecerere" : "41774.0"
      }
    }, {```
