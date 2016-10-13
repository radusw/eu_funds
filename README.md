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
