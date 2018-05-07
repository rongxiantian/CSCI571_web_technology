var express = require('express');
var app = express();
var port = process.env.PORT ||3000;
var https= require("https");
var url= require("url");
var request = require("request");

app.get('/location_cur', function (req, res) {
    res.setHeader("Content-Type","text/plain");
    res.setHeader("Access-Control-Allow-Origin","*");
    var params = url.parse(req.url, true).query;
    var url_text='https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='
        +params.location_lat+','+params.location_lng+'&radius='+params.Distance*1609.34+'&type='+params.Category+'&keyword='
        +params.Keyword+'&key=AIzaSyCaVHSWA2FTNKYLOecEY05UcAz9yo1krzA';
    https.get(url_text,function(req2,res2){
        var res_text = "";
        req2.on('data',function(data){
            res_text+=data;
        });
        req2.on('end',function(){
            return res.send(res_text);
        });

    });
    console.log("location_cur GET");
    //res.send('Hello GET');
})

app.get('/next',function (req, res) {
    res.setHeader("Content-Type","text/plain");
    res.setHeader("Access-Control-Allow-Origin","*");
    var params = url.parse(req.url, true).query;
    var url_text='https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken='
        +params.pagetoken+'&key=AIzaSyCaVHSWA2FTNKYLOecEY05UcAz9yo1krzA';
    https.get(url_text,function(req2,res2){
        var res_text = "";
        req2.on('data',function(data){
            res_text+=data;
        });
        req2.on('end',function(){
            return res.send(res_text);
        });

    });
    console.log("next table GET");
})

app.get('/Yelpbest',function(req, res){
    res.setHeader("Content-Type","text/plain");
    res.setHeader("Access-Control-Allow-Origin","*");
    var params = url.parse(req.url, true).query;
    var options = {
        method: 'GET',
        url: 'https://api.yelp.com/v3/businesses/matches/best',
        qs:
            {
                name: params.name,
                city: params.city,
                state: params.state,
                country: 'US',
                address1: params.address1
            },
        headers:
            {
                'Cache-Control': 'no-cache',
                Authorization: 'Bearer omHtyliNT2Etr9Glqadcy7V6-gGc0S8J9QmGrNK_o66WmpETw7K-JYqP2TQcjSLlhRvB89aZ0KZohaLIT4VNoswNUAQq1TGELbo8oQwoQI3YCs5wu30LjAM6FfvLWnYx'
            }
    };
    request(options, function (error, response, body) {
        res.send(body);
    });

})

app.get('/Yelpreviews',function(req, res){
    res.setHeader("Content-Type","text/plain");
    res.setHeader("Access-Control-Allow-Origin","*");
    var params = url.parse(req.url, true).query;
    var options = {
        method: 'GET',
        url: 'https://api.yelp.com/v3/businesses/'+params.id+'/reviews',

        headers:
            {
                'Cache-Control': 'no-cache',
                Authorization: 'Bearer omHtyliNT2Etr9Glqadcy7V6-gGc0S8J9QmGrNK_o66WmpETw7K-JYqP2TQcjSLlhRvB89aZ0KZohaLIT4VNoswNUAQq1TGELbo8oQwoQI3YCs5wu30LjAM6FfvLWnYx'
            }
    };
    request(options, function (error, response, body) {
        res.send(body);
    });

})

/*
app.get('/Yelpbest',function (req, res) {


    var params = url.parse(req.url, true).query;
    var url_text='https://api.yelp.com/v3/businesses/matches/best?name='
        +params.name+'&address1'+params.address1+'&city' +params.city+'&state' + params.state
        + '&country=US&key=omHtyliNT2Etr9Glqadcy7V6-gGc0S8J9QmGrNK_o66WmpETw7K-JYqP2TQcjSLlhRvB89aZ0KZohaLIT4VNoswNUAQq1TGELbo8oQwoQI3YCs5wu30LjAM6FfvLWnYx';
    https.get(url_text,function(req2,res2){
        var res_text = "";
        req2.on('data',function(data){
            res_text+=data;
        });
        req2.on('end',function(){
            return res.send(res_text);
        });

    });
})*/


var server = app.listen(port, function () {

    var host = server.address().address
    var port = server.address().port

    console.log("Example app listening at http://%s:%s", host, port)
})