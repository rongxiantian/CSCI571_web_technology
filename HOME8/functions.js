var app = angular.module('form_app', ['gm','ngMap','ngAnimate']);
app.controller('bodyCtrl', function($scope, $http,$animate) {

    //test
    //angular.element( document.querySelector( '#map_content' )).innerHTML = "test";

    $scope.initial = {Keyword:"", Category:"Default", Distance:"10"};
    $scope.reset = function() {
        $scope.user = angular.copy($scope.initial);
        $scope.table = [];
        for (var i = 0; i < 3; i++)
            $scope.table.push({table_content:undefined,table_statuscode:undefined,table_statustext:undefined});
        $scope.totalpage = 0;
        $scope.location="location_cur";
        $scope.show_favour = false;
        $scope.detail_button = false; //disable detail button
        $scope.show_detail = false;//hide detail form

        $scope.curhigh = "";//for highlight column
        $scope.showYelp = false;
    };
    $scope.reset();


    //autocomplete
    $scope.lat = undefined;
    $scope.lng = undefined;

    $scope.$on('gmPlacesAutocomplete::placeChanged', function(){
        var location = $scope.location_oth_t.getPlace().geometry.location;
        $scope.lat = location.lat();
        $scope.lng = location.lng();
    });
    //get_cur_location
    $scope.cur_content=undefined;
    $http.get("https://ipapi.co/json/")
        .then(function(response) {
            $scope.cur_content = response.data;
            $scope.cur_statuscode = response.status;
            $scope.cur_statustext = response.statusText;
        });

    $scope.table= [];
    $scope.totalpage = 0;
    for (var i = 0; i < 3; i++)
        $scope.table.push({table_content:undefined,table_statuscode:undefined,table_statustext:undefined});
    //get table data
    $scope.location="location_cur";
    $scope.progress = false;
    $scope.SearchFunc = function () {
        $scope.progress = true;
        var params="";
        if($scope.location==="location_cur"){
             params += "Keyword=" + $scope.user.Keyword + '&' +
                "Category=" + $scope.user.Category+ '&' +
                "Distance=" + $scope.user.Distance+ '&' +
                "location_lat=" +$scope.cur_content.latitude + '&' +
                "location_lng=" +$scope.cur_content.longitude;
        }
        else if($scope.location==="location_oth"){
            params += "Keyword=" + $scope.user.Keyword + '&' +
                "Category=" + $scope.user.Category+ '&' +
                "Distance=" + $scope.user.Distance+ '&' +
                "location_lat=" +$scope.lat + '&' +
                "location_lng=" +$scope.lng;
        }
        $http.get("http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/location_cur?"+params)
            .then(function(response) {
                //console.log(typeof response.data);
                $scope.table[0].table_content = response.data.results;
                $scope.table[0].table_statuscode = response.status;
                $scope.table[0].table_statustext = response.statusText;
                $scope.next_page_token = response.data.next_page_token;
                $scope.curpage = 0;
                $scope.detail_button = true;
                $scope.progress = false;
            });
    }

    //get next page data
    $scope.NextFunc = function (npage) {
        var params = "pagetoken=" + $scope.next_page_token;
        if($scope.table[npage].table_statuscode===undefined){
            $http.get("http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/next?"+params)
                .then(function(response) {
                    //console.log(typeof response.data);
                    $scope.table[npage].table_content = response.data.results;
                    $scope.table[npage].table_statuscode = response.status;
                    $scope.table[npage].table_statustext = response.statusText;
                    $scope.next_page_token = response.data.next_page_token;
                    $scope.curpage = npage;
                });
        }
        else{
            $scope.curpage = npage;
            $scope.$apply();
        }
    }

    $scope.GoogFunc = function () {
        $scope.showYelp = false;
    }

    $scope.YelpFunc = function () {
        if($scope.Yelp==undefined){
            if($scope.d_name!=undefined && $scope.d_Address!=undefined){
                var params = "name=" + $scope.d_name+"&address1=" + $scope.d_Address1 + '&city=' + $scope.d_city + '&state=' + $scope.d_state;
                $http.get("http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/Yelpbest?"+params)
                    .then(function (response) {
                        var params2 = "id=" +response.data.businesses[0].id;
                        $http.get("http://rongxiat-hw7.us-west-1.elasticbeanstalk.com/Yelpreviews?"+params2)
                            .then(function (response2) {
                                $scope.Yelp = response2.data.reviews;
                                $scope.showYelp = true;
                                /*
                                for(i=0;i<$scope.Yelp.length;i++){
                                    starTx = "";
                                    for(j = 0; j< Math.round($scope.Yelp[i].rating);j++){
                                        starTx+="<i class=\"fa fa-star\"></i>";
                                    }
                                    document.getElementById($scope.Yelp[i].id).innerHTML+=starTx;
                                }*/

                            });
                    });
            }
            else{
                return;
            }

        }
        else{
            $scope.showYelp = true;
        }



    }
    
    
    
    
    $scope.PreviousFunc = function (ppage) {
        $scope.curpage = ppage;
        $scope.$apply();
    }

    //favourite table
    $scope.favour = {};

    $scope.show_favour = false;
    for(x in localStorage){//x is place_id
        $scope.favour[x]= JSON.parse(localStorage.getItem(x));
    }
    $scope.hasfavour = !angular.equals($scope.favour,{});
    //console.log(typeof $scope.favour);
    $scope.set_favour = function (x) {
        if(document.getElementById(x.place_id).classList.contains("fa-star-o")){
            document.getElementById(x.place_id).classList.remove("fa-star-o");
            document.getElementById(x.place_id).classList.add("fa-star");
            localStorage.setItem(x.place_id,JSON.stringify(x));
            $scope.favour[x.place_id]=x;
        }else{
            document.getElementById(x.place_id).classList.remove("fa-star");
            document.getElementById(x.place_id).classList.add("fa-star-o");
            localStorage.removeItem(x.place_id);
            delete $scope.favour[x.place_id];

        }
        $scope.hasfavour = !angular.equals($scope.favour,{});
        $scope.$apply();

    }
    $scope.remove_favour = function (x,i) {
        localStorage.removeItem(x.place_id);
        delete $scope.favour[x.place_id];
        $scope.hasfavour = !angular.equals($scope.favour,{});
        $scope.$apply();
    }
    $scope.ResultToFavor = function () {
        $scope.show_favour = true;
        $scope.show_detail = false;
    }
    $scope.FavorToResult = function () {
        $scope.show_favour = false;
        $scope.show_detail = false;
    }


    //control list and detail button
    $scope.detail_button = false; //disable detail button
    $scope.show_detail = false;//hide detail form

    $scope.curhigh = "";//for highlight column

    //get details and photos
    $scope.get_detail = function (placeid, location,x) {//location is an object of {lat:num,lng:num}
        //$scope.detail_button = true;
        $scope.show_detail = true;
        $scope.curX = x;//for detail use
        if($scope.curX.place_id in $scope.favour){
            document.getElementById("d_star").classList.remove("fa-star-o");
            document.getElementById("d_star").classList.add("fa-star");
        }
        else{
            document.getElementById("d_star").classList.remove("fa-star");
            document.getElementById("d_star").classList.add("fa-star-o");
        }
        if($scope.curhigh!=""){
            document.getElementById($scope.curhigh).classList.remove('bg-warning');
        }
        $scope.curhigh = placeid+'_t';
        document.getElementById($scope.curhigh).classList.add('bg-warning');



        var request = {placeId: placeid};
        map_des_loc = location;
        function callback(place, status) {
            if (status == google.maps.places.PlacesServiceStatus.OK) {
                //this is for form
                $scope.detail_form = {d_status:true};
                if(place.formatted_address!=undefined){
                    $scope.detail_form.d_Address = place.formatted_address;
                }
                if(place.name!=undefined){
                    $scope.detail_form.d_name = place.name;
                }
                if(place.d_Phone_Number!=undefined){
                    $scope.detail_form.d_Phone_Number = place.international_phone_number;
                }
                if(place.d_Price_Level!=undefined){
                    $scope.detail_form.d_Price_Level = place.price_level;
                }
                if(place.d_Rating!=undefined){
                    $scope.detail_form.d_Rating = place.rating;
                }
                if(place.d_Google_Page!=undefined){
                    $scope.detail_form.d_Google_Page = place.url;
                }
                if(place.d_Website!=undefined){
                    $scope.detail_form.d_Website = place.website;
                }
                $scope.d_status = true;
                $scope.d_Address = place.formatted_address;
                $scope.d_name = place.name;
                $scope.d_Phone_Number = place.international_phone_number;
                $scope.d_Price_Level = "";
                for(i=0;i<place.price_level;i++){
                    $scope.d_Price_Level+='$';
                }

                $scope.d_Rating = place.rating;

                //to change rating into star
                $scope.starT = "";
                for(i = 0; i< Math.round($scope.d_Rating);i++){
                    $scope.starT+="<i class=\"fa fa-star\"></i>";
                }
                document.getElementById('Rating_star').innerHTML = $scope.starT;
                //

                $scope.d_Google_Page = place.url;
                $scope.d_Website = place.website;
                if('opening_hours' in place){
                    //get local time
                    $scope.d_weekday_text = place.opening_hours.weekday_text;
                    day = new Date().getDay();
                    document.getElementById('day'+day).classList.add('font-weight-bold');
                    if(place.opening_hours.open_now==true){
                        d = new Date().getDay();
                        $scope.d_Hours = "Open now: " + place.opening_hours.weekday_text[d].split(": ")[1];
                    }
                    else{
                        $scope.d_Hours = "Closed";
                    }
                }
                else{
                    $scope.d_Hours =undefined;
                }

                //for Yelp
                for( i = 0;i< place.address_components.length;i++){
                    if(place.address_components[i].types[0]=='administrative_area_level_1'){
                        $scope.d_state = place.address_components[i].short_name;
                    }
                    else if(place.address_components[i].types[0]=='administrative_area_level_2'){
                        $scope.d_city = place.address_components[i].short_name;
                    }
                }
                $scope.showYelp = false;
                $scope.d_Address1 = place.vicinity;

                //this is for photo
                $scope.photos=[];
                for(i = 0; i<place.photos.length;i++ ){

                    $scope.photos.push(place.photos[i].getUrl({'maxWidth': 600}));
                }

                //get map
                $scope.get_cur_loc = function(){

                    $scope.cur_loc = null;

                    if($scope.location=="location_cur"){
                        $scope.cur_loc = [$scope.cur_content.latitude,$scope.cur_content.longitude];
                    }
                    else{
                        $scope.cur_loc =[$scope.lat,$scope.lng];

                    }
                    $scope.map_cur_loc = "";
                    $scope.map_des_loc = $scope.d_name+','+$scope.d_Address;
                    $scope.map_travel_mode = "DRIVING";


                }
                $scope.get_cur_loc();

                //get review
                $scope.d_reviews = place.reviews;
                $scope.d_reviews.forEach(function (re) {
                    if(re.time!=undefined){
                        var d = new Date(re.time*1000);
                        re.time = ""+ d.getFullYear() +"-"+ (d.getMonth()+1)+"-"+d.getDate()+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
                    }
                })
                /*for(var re in $scope.d_reviews){
                    if(re.time!=undefined){
                        var d = new Date(re.time);
                        re.time = ""+ d.getFullYear() +"-"+ d.getMonth()+"-"+d.getDay()+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
                    }
                }*/
                //twitter href
                $scope.twitter_text = "https://twitter.com/intent/tweet?text=Check out "+$scope.d_name+" locate at "+$scope.d_Address+". Website:"+$scope.d_Website+" #TravelAndEntertainmentSearch";
                $scope.$apply();
            }

        }
        service = new google.maps.places.PlacesService(document.createElement('div'));
        service.getDetails(request, callback);

        //$scope.initmap(cur_loc,map_des_loc);

    }
    $scope.recent_detail = function () {
        $scope.show_detail = true;
        $scope.$apply();
    }
    $scope.ListFunc = function () {
        $scope.show_detail = false;
        $scope.$apply();
    }


    $scope.map_cur_loc_copy = "";
    $scope.map_des_loc_copy = "";
    $scope.map_travel_mode_copy = "";

    $scope.GetDirect = function () {
        $scope.map_cur_loc_copy = $scope.map_cur_loc;
        $scope.map_des_loc_copy = $scope.map_des_loc;
        $scope.map_travel_mode_copy = $scope.map_travel_mode;
        //$scope.$apply();
    }

    $scope.show_street = false;
    $scope.streetFunc = function () {
        if($scope.show_street==false){
            $scope.show_street = true;
            document.getElementById("street").src = 'Map.png';

        }
        else{
            $scope.show_street = false;
            document.getElementById("street").src = 'Pegman.png';
        }

    }
    
    
    $scope.sortBy = "";
    $scope.rever = false;
    $scope.sortByFunc = function (sort,r) {
        $scope.sortBy = sort;
        $scope.rever = r;
    }
    
    
    /*$scope.initmap = function(cur_loc,des_loc){

        //cur_loc ={lat:map_lat, lng:map_lng};
        //des_loc = map_des_loc;

        var directionsService = new google.maps.DirectionsService;
        var directionsDisplay = new google.maps.DirectionsRenderer;


        var map = new google.maps.Map(angular.element( document.querySelector( '#map_content' )), {
            zoom: 13,
            center: cur_loc
        });
        var marker = new google.maps.Marker({
            position: cur_loc,
            map: map
        });

        directionsDisplay.setMap(map);

    }*/
    //$scope.initmap();



});

