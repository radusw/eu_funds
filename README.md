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

## Example
### How many funds accessed in an area:
- /list?filter=Constanta&from=0&size=200
```javascript
"hits" : { "total" : 61527, ...
```

### Search by company name:
- /list?filter=Titluproiuect:hotel Criss&from=0&size=100
```javascript
RichSearchResponse({
  "took" : 19,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 4,
    "failed" : 0
  },
  "hits" : {
    "total" : 3537,
    "max_score" : 3.530158,
    "hits" : [ {
      "_index" : "gov",
      "_type" : "funds",
      "_id" : "AVe_H1aev5_7zlYBOQRH",
      "_score" : 3.530158,
      "_source" : {
        "Titluproiuect" : "Extindere hotel Criss Bucuresti",
        "Dataautorizata" : "1.0",
        "Motivintarziere" : "BUCURESTI - ILFOV",
        "Cheie" : "5207\\20",
        "CodPO" : "MUNICIPIUL BUCURESTI",
        "Codbeneficiar" : "BUCURESTI",
        "Valoarerambursata" : "4.0",
        "CodSMIS" : "5207.0",
        "Dataultimeiplati" : "5.2",
        "NrAutorizare" : "20.0",
        "Valoareeligibilaceruta" : "5002.0",
        "Numebeneficiar" : "Crossline srl",
        "Valoareautorizata" : "5.0",
        "Datadepunerecerere" : "41754.0"
      }
    }, {
      "_index" : "gov",
      "_type" : "funds",
      "_id" : "AVe_IC9Iv5_7zlYBPNef",
      "_score" : 3.530158,
      "_source" : {
        "Titluproiuect" : "Extindere hotel Criss Bucuresti",
        "Dataautorizata" : "1.0",
        "Motivintarziere" : "BUCURESTI - ILFOV",
        "Cheie" : "5207\\20",
        "CodPO" : "MUNICIPIUL BUCURESTI",
        "Codbeneficiar" : "BUCURESTI",
        "Valoarerambursata" : "4.0",
        "CodSMIS" : "5207.0",
        "Dataultimeiplati" : "5.2",
        "NrAutorizare" : "20.0",
        "Valoareeligibilaceruta" : "5002.0",
        "Numebeneficiar" : "Crossline srl",
        "Valoareautorizata" : "5.0",
        "Datadepunerecerere" : "41754.0"
      }
    }, {
    ...
```
