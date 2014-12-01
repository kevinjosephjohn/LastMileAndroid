var express = require('express')
var bodyParser = require('body-parser')
var http = require('http')
var gm = require('googlemaps');
var util = require('util');

var app = express()

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

app.use(function (req, res) {
  res.setHeader('Content-Type', 'text/plain')

  var origin_lat = req.body.origin_lat;
  var origin_lng = req.body.origin_lng;
  var origin_location = origin_lat + "," + origin_lng ;

  var destination_lat = req.body.destination_lat;
  var destination_lng = req.body.destination_lng;
  var destination_location = destination_lat + "," + destination_lng ;
  
  gm.directions(origin_location,destination_location, function(err, data){
  var details = JSON.stringify(data);
  var obj = JSON.parse(details);
  res.end(details)
});
  
})

//create node.js http server and listen on port
http.createServer(app).listen(80)