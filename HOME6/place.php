<?php

$errors = array();
$message = "";
$key = "AIzaSyCaVHSWA2FTNKYLOecEY05UcAz9yo1krzA";
$GoogleMapGeocoding_text = "";
$GooglePlacesNearbySearch_text = "";
$GoogleMapGeocoding_Location="";
$GoogleMapGeocoding_LatLng=array();
$GPN_to_JS_JSON = "";

if(isset($_GET["Keyword"])&&isset($_GET["Category"])&&isset($_GET["Distance"])&&
	((isset($_GET["location_other"])&&!isset($_GET["location_lat_here"]))||(!isset($_GET["location_other"])&&isset($_GET["location_lat_here"])))) {//XOR

	$Keyword = $_GET["Keyword"];
	$Category= $_GET["Category"];
	$Distance= $_GET["Distance"];

	$Distance = $Distance*1609.34;

	if(isset($_GET["location_other"])){
		$location_other= $_GET["location_other"];
		$location_other = str_replace(' ', '+', $location_other);

		$GoogleMapGeocoding_text.="https://maps.googleapis.com/maps/api/geocode/json?"
								. "address=".$location_other."&key=".$key;

		$GoogleMapGeocoding = file_get_contents($GoogleMapGeocoding_text); 
		$GMGeocoding_decode = json_decode($GoogleMapGeocoding, true);


		if($GMGeocoding_decode["status"]=="OK"){
			$GoogleMapGeocoding_Location = $GMGeocoding_decode["results"][0]["geometry"]["location"]["lat"].",". 
									   	   $GMGeocoding_decode["results"][0]["geometry"]["location"]["lng"];
			$GoogleMapGeocoding_LatLng['lat'] = $GMGeocoding_decode["results"][0]["geometry"]["location"]["lat"];
			$GoogleMapGeocoding_LatLng['lng'] = $GMGeocoding_decode["results"][0]["geometry"]["location"]["lng"];
		}
		else{
			echo json_encode(array());
			return false;
		}
		
			
	}
	else{//given cur location
		$location_lat_here = $_GET["location_lat_here"];
		$location_lng_here = $_GET["location_lng_here"];
		$GoogleMapGeocoding_Location = $location_lat_here.",".$location_lng_here;
		$GoogleMapGeocoding_LatLng['lat'] = $location_lat_here;
		$GoogleMapGeocoding_LatLng['lng'] = $location_lng_here;

	}


						   
	$GooglePlacesNearbySearch_text.="https://maps.googleapis.com/maps/api/place/nearbysearch/json?" . "location=" . 
									 $GoogleMapGeocoding_Location . "&radius=" . $Distance . "&type=" . $Category . 
									 "&keyword=" . $Keyword . "&key=" . $key;
	$GooglePlacesNearbySearch = file_get_contents($GooglePlacesNearbySearch_text);


	//this is an array
	$GPNearbySearch_JSON = json_decode($GooglePlacesNearbySearch, true);
	$GPN_to_JS = array();

	foreach ($GPNearbySearch_JSON["results"] as $i => $result){
		$GPN_to_JS[$i]["icon"] = $result["icon"];
		$GPN_to_JS[$i]["name"] = $result["name"];
		$GPN_to_JS[$i]["vicinity"] = $result["vicinity"];
		$GPN_to_JS[$i]["place_id"] = $result["place_id"];
		$GPN_to_JS[$i]["LatLng_cur"] = $GoogleMapGeocoding_LatLng;//this is current location
		$GPN_to_JS[$i]["LatLng_des"] = array("lat"=>$result["geometry"]["location"]["lat"],"lng"=>$result["geometry"]["location"]["lng"]);//this is destination location
	}
	$GPN_to_JS_JSON = json_encode($GPN_to_JS);
	echo $GPN_to_JS_JSON;
	return false;

}
else if (isset($_GET["place_id"])) {
	$place_id = $_GET["place_id"];

	$GooglePlacesDetails_text = "https://maps.googleapis.com/maps/api/place/details/json?" 
							. "placeid=" . $place_id . "&key=".$key;
	$GooglePlacesDetails = file_get_contents($GooglePlacesDetails_text); 
	$GooglePlacesDetails_JSON = json_decode($GooglePlacesDetails, true);//this is an array

	$GP_Photos = array();//final result for photo
	$GooglePlacesReviews = array(array());//final result for review 	
	$GooglePlacesName="";//final result for name

	if($GooglePlacesDetails_JSON["status"]=="OK"){// there are results for photos or/and results 

		if(isset($GooglePlacesDetails_JSON["result"]["photos"])){
			//get photos
			$GooglePlacesPhotos_text = "";
			$GooglePlacesPhotos = array();//store at most 5 photos 
		 	
			foreach($GooglePlacesDetails_JSON["result"]["photos"] as $i => $photo){
				if($i>=5) break;
				$GooglePlacesPhotos_text = "https://maps.googleapis.com/maps/api/place/photo?"
										. "maxwidth=" .$photo["width"]."&maxheight=" .$photo["height"]
										."&photo_reference=" .$photo["photo_reference"]. "&key=".$key;
				$GooglePlacesPhoto_single = file_get_contents($GooglePlacesPhotos_text); 						
				array_push($GooglePlacesPhotos, $GooglePlacesPhoto_single);

			}


			if(!file_exists("/home/scf-11/rongxiat/public_html/HOME5/apache2/htdocs/".$place_id)){
				mkdir("/home/scf-11/rongxiat/public_html/HOME5/apache2/htdocs/".$place_id);
			}

			foreach($GooglePlacesPhotos as $i => $photo){
				if($i>=5) break; 

				$path = "/home/scf-11/rongxiat/public_html/HOME5/apache2/htdocs/".$place_id."/". $place_id."_".$i.".jpg";
				file_put_contents($path, $photo);

			}

			//get photos and reviews to be json file then send to client
			
			for($i=0;$i<5;$i++){//get images from server


				$GP_Photos[$i] = $place_id."/". $place_id."_".$i.".jpg";
			}

		}
		else{
			$GP_Photos[0]=-1;
		}

		//get reviews
		if(isset($GooglePlacesDetails_JSON["result"]["reviews"])){
			foreach($GooglePlacesDetails_JSON["result"]["reviews"] as $i => $review){
				if($i>=5) break;
				$GooglePlacesReviews[$i][0] = $review["author_name"];
				$GooglePlacesReviews[$i][1] = $review["profile_photo_url"];
				$GooglePlacesReviews[$i][2] = $review["text"];
			}			
		}
		else{
			$GooglePlacesReviews[0]=array(-1,-1,-1);
		}

		//get name
		if(isset($GooglePlacesDetails_JSON["result"]["name"])){
			$GooglePlacesName = $GooglePlacesDetails_JSON["result"]["name"]; 
		}
		else{
			echo json_encode(array(-1,array(-1,-1,-1),-1));
			return false;
		}


		$GP_Photos_Reviews_to_JS_JSON = json_encode(array($GP_Photos, $GooglePlacesReviews,$GooglePlacesName));
		echo $GP_Photos_Reviews_to_JS_JSON;
		return false;
	}
	else{//no results for photos and reviews
		echo json_encode(array(-1,array(-1,-1,-1),-1));
		return false;
	}

}
else{
	$message = "Please push submit";
	
}

?>
<!DOCTYPE html>
<html lang="en">
<head>
	<style type="text/css">
		table.tabular{
			margin-top: 15px;
			margin: auto;
			width:70%;
		}
		table.review{
			margin-top: 15px;
			margin: auto;
			width:810px;	
			text-align: center;
		}
		table.photo{
			margin-top: 15px;
			margin: auto;
			width:810px;	
			text-align: center;
		}		
		td, th {
		    border: 1px solid #dddddd;
		    text-align: center;
		    height: 20px;
		    padding: 8px;
		}		
		th{
			text-align: center;
		}

		form{
			 display: block;
			 margin: auto;
			 width: 650px;
			 background-color: #fafafa;
		     border: 5px solid #cccccc;
		     margin-bottom: 15px;

		}
		form label{
			margin-left: 5px;
			font-weight: bold;
		}

		tr.center_tr{
			display: block;
			margin: auto;
		}
		.tabular_and_map_class{
		position: relative;	
		}
		.map_class {
		position: absolute;
		top: 80px;
		left: 1070px;
        height: 355px;
        width: 410px;
        z-index: 1;
        display: none;
       }
       .center_div{
			display: block;       		
   			margin: auto;
       }
       .center_img{
   			width:45px;
   			height:45px;
   			margin: auto;
   			display:none;
       }
       .center_text{
       		margin: auto;
   			display:none;
       }
       .travel_way_button{
       	position: absolute;
       	top:80px;
       	left:1070px;
       	height:30px;
       	width:100px;
       	z-index: 2;
       	background-color: #fff;
       	display: none;
       }
       #Search_Keyword{
       		text-align: center;
       	    margin-top: 10px;
       		font-weight: bold;
       		font-size: 20px;
       		margin-bottom: 10px;
       }
	</style>
	<meta charset="utf-8">

	<script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCaVHSWA2FTNKYLOecEY05UcAz9yo1krzA&callback=initMap">
    </script>
</head>
<body>

	<form onsubmit="return false" > 
		<h3 style="margin-top: 5px; text-align: center; font: italic bold 25px/30px Georgia, serif;">Travel and Entertainment Search</h3>	
		<label>Keyword </label><input type="text" id="Keyword" required><br>
		<label>Category </label><select type="text" id="Category">
			<option value="default" selected>default</option>
			<option value="cafe">cafe</option>
			<option value="bakery">bakery</option>
			<option value="restaurant">restaurant</option>
			<option value="beauty_salon">beauty salon</option>
			<option value="casino">casino</option>
			<option value="movie_theater">movie theater</option>
			<option value="lodging">lodging</option>
			<option value="airport">airport</option>
			<option value="train_station"> train station</option>
			<option value="subway_station">subway station</option>
			<option value="bus_station">bus station</option>
		</select><br> 
		<label>Distance(miles) </label><input type="text" id="Distance" placeholder="10">	
		<label>from </label>
		<input type="radio" name="location" id="location_Here" value="Here" checked>Here<br>
		<input style="margin-left: 48%;" type="radio" name="location" id="location_other"> <input type="text" id="location_other_text" placeholder="location" disabled>
		
		<div style="margin-left: 30px;margin-top: 30px; margin-bottom: 30px;">
		<input type="submit" id="Search" value="Search" onclick="get_tabular()" disabled>   <input type="reset" id="Clear" value="Clear"  onclick="clear_form()">
		</div>
	</form>



	<div id="tabular_and_map" class="tabular_and_map_class">	
		<div id="tabular" class="center_div"></div>
		<div id="map" class="map_class"></div>
		<button id="tr_button1" class="travel_way_button" style="top:0;">Walk there</button>
		<button id="tr_button2" class="travel_way_button" style="top:30px;">Bike there</button>
		<button id="tr_button3" class="travel_way_button" style="top:60px;">Drive there</button>
	</div>
	<div id="Search_Keyword"></div>
	<div id="review_button" style="text-align: center">   
			<p id="review_hide" class="center_text">click to hide reviews</p><p id="review_show" class="center_text">click to show reviews</p>
			<img id="review_arrow_down" class="center_img" src="arrow_down.png" onclick="arrow_up_down(false)">
			<img id="review_arrow_up" class="center_img" src="arrow_up.png" onclick="arrow_up_down(false)"></div>

	<div id="reviews" class="center_div" style="display: none;"></div>
	<div id="image_button"  style="text-align: center">	
			<p id="photo_hide" class="center_text">click to hide photos</p><p id="photo_show" class="center_text">click to show photos</p>
			<img id="image_arrow_down" class="center_img"  src="arrow_down.png" onclick="arrow_up_down(true)">
			<img id="image_arrow_up" class="center_img" src="arrow_up.png" onclick="arrow_up_down(true)"></div>

	<div id="images" class="center_div" style="display: none;"></div>
</body>
<script type="text/javascript">

	function clear_form(){
		if(document.getElementById("location_Here").checked = true){
			document.getElementById("location_other_text").disabled = true;
		}
	}

   		function reset_button_and_map() {
   			document.getElementById("map").style.display="none";
	        document.getElementById("tr_button1").style.display="none";
	        document.getElementById("tr_button2").style.display="none";     	
	        document.getElementById("tr_button3").style.display="none";	
	        document.getElementById("review_hide").style.display="none";	
	        document.getElementById("review_show").style.display="none";	
	        document.getElementById("review_arrow_down").style.display="none";	
	        document.getElementById("review_arrow_up").style.display="none";
	        document.getElementById("photo_hide").style.display="none";	
	        document.getElementById("photo_show").style.display="none";	
	        document.getElementById("image_arrow_down").style.display="none";	
	        document.getElementById("image_arrow_up").style.display="none";	
   		}
</script>
<script type="text/javascript">
		function get_cur_loc(){//get_current_location
			var ipcom = new XMLHttpRequest();
			ipcom.open("GET", "https://ipapi.co/json/", false);
			ipcom.send();
			console.log(JSON.parse(ipcom.responseText));
			
			document.getElementById("Search").disabled = false;

			return [JSON.parse(ipcom.responseText).latitude,JSON.parse(ipcom.responseText).longitude];
		}
		var cur_loc = get_cur_loc();//LatLng
		var location_other="";
		var location_lat_here="";//need be cleaned when reset the search
		var location_lng_here="";
   		function get_tabular(){
   			//reset
   			document.getElementById("Search_Keyword").innerHTML=""; 
   			document.getElementById("Search_Keyword").style.display="none"; 
   			document.getElementById("image_button").style.display="none"; 
   			document.getElementById("images").style.display="none"; 
   			document.getElementById("review_button").style.display="none"; 
   			document.getElementById("reviews").style.display="none"; 
			reset_button_and_map();		

   			//set distance
   			if(document.getElementById("Distance").value==""){
   				document.getElementById("Distance").value=10;
   			}

   			if(document.getElementById("Keyword").value==""){
   				return;
   			}
   			var param="";
   			if(document.getElementById("location_Here").checked == true){
   				location_lat_here = cur_loc[0];
   				location_lng_here = cur_loc[1];	
   				var param = "Keyword=" + document.getElementById("Keyword").value + '&' +
   						"Category=" + document.getElementById("Category").value+ '&' +
   						"Distance=" + document.getElementById("Distance").value+ '&' +
   						"location_lat_here=" +location_lat_here + '&' +
   						"location_lng_here=" +location_lng_here;	

   			}
   			else if(document.getElementById("location_other").checked == true){
   				location_other = document.getElementById("location_other_text").value;
   				location_other = location_other.split(' ').join('+');
	   			var param = "Keyword=" + document.getElementById("Keyword").value + '&' +
	   						"Category=" + document.getElementById("Category").value+ '&' +
	   						"Distance=" + document.getElementById("Distance").value+ '&' +
	   						"location_other=" + location_other;
   			}
   			else {
   				console.log("no checked value")
   			}
   			var xmlhttp = new XMLHttpRequest();
	    	xmlhttp.open("GET", "place.php?"+param, false);
			xmlhttp.send();

			var tabular_text="";
			if(xmlhttp.responseText!="[]"){
				tabular= JSON.parse(xmlhttp.responseText);
				tabular_text += "<table class='tabular'><tr><th style='width:200px;'>Category</th><th style='width:700px;'>Name</th><th style='width:700px;'>Address</th></tr>";

		   		for(var i = 0;i<tabular.length;i++){//output the table
		   			tabular_text+=  "<tr>" + "<td>" + "<img src='" + tabular[i]["icon"]    + "' style='width:30px;height:30px;' >"+ "</td>" + 
		   									 "<td>" + "<a href=\'javascript:get_review_photo(\""  + tabular[i]["place_id"] + "\");\'>"+ tabular[i]["name"] +"</td>" + 
		   							 		 "<td>" + "<a id=\"address_" + i + "\"  href=\'javascript:initMap(" + i + 
		   							 		 ", {lat:" + tabular[i]["LatLng_cur"]["lat"] + ",lng:" + tabular[i]["LatLng_cur"]["lng"] +
		   							 		 "}, {lat:" + tabular[i]["LatLng_des"]["lat"] + ",lng:" + tabular[i]["LatLng_des"]["lng"] +"});\'>"  
		   							 		 +	tabular[i]["vicinity"]  +  "</td>"+ 
		   							"</tr>";
		   		}
		   		tabular_text+="</table>";
		   		}
			else{
				tabular_text+="<table class='tabular'><tr><th style='text-align:center;background-color:#cccccc;'><strong>No Records has been found</strong></th></tr></table>"
			}
			

			
	   		document.getElementById("tabular").innerHTML = tabular_text;
   		}



   		function get_review_photo(place_id){
   			//disable the tabular
   			document.getElementById("tabular").innerHTML = "";
			reset_button_and_map();		

   			//reset
   			document.getElementById("Search_Keyword").style.display="block"; 
   			document.getElementById("image_button").style.display="block"; 
   			document.getElementById("review_button").style.display="block"; 
  			

   			var rev_pho = new XMLHttpRequest();
	    	rev_pho.open("GET", "place.php?"+"place_id="+place_id, false);
			rev_pho.send();
			rev_pho_text =  JSON.parse(rev_pho.responseText);

			//if there is no photo/review, it will return [-1,[-1,-1,-1],-1]

			//write photo tabular
			photo_text = "<table class='photo'>";
			if(rev_pho_text[0]!=-1){
				for(var i = 0 ; i<rev_pho_text[0].length;i++){//rev_pho_text[0] is for photo url	
					photo_text+="<tr><td>" + "<a target=\"_blank\" href='" + rev_pho_text[0][i] + "'>" +
					"<img src='" + rev_pho_text[0][i] +"' style='width:810px;height:550px;'>"+ "</td></tr>";	
				}
				photo_text+="</table>";	
			}
			else{
				photo_text +="<tr><td><strong>No Photos Found</strong></td></tr></table>";
			}
			document.getElementById("images").innerHTML = photo_text;



			//write review tabular
			review_text = "<table class='review'>";
			if(rev_pho_text[1][0][0]!=-1){
				for(var i = 0 ; i<rev_pho_text[1].length;i++){//rev_pho_text[1][0,1,2] is for review name, image_url, comment
					review_text+="<tr><td>" + "<img src='" + rev_pho_text[1][i][1] + "' style='width:45px;height:45px;'>" +
									   	  rev_pho_text[1][i][0]+ "</td>" + 
								"</tr><tr>" + "<td style='text-align:left;'>" + rev_pho_text[1][i][2];	
				}
				review_text+="</table>";	
			}
			else{
				review_text+="<tr><td><strong>No Reviews Found</strong></td></tr></table>";
			}
			document.getElementById("reviews").innerHTML = review_text;

			//write name 
			document.getElementById("Search_Keyword").innerHTML = rev_pho_text[2];

			document.getElementById("image_arrow_down").style.display = "block";
			document.getElementById("review_arrow_down").style.display = "block";	
			document.getElementById("photo_show").style.display = "block";
			document.getElementById("review_show").style.display = "block";	
					

   		}
</script>
<script>
   		function arrow_up_down(image_or_review){//true for image, false for review
   			var image_arrow_down = document.getElementById("image_arrow_down");
   			var image_arrow_up   = document.getElementById("image_arrow_up");
   			var review_arrow_down= document.getElementById("review_arrow_down");
   			var review_arrow_up  = document.getElementById("review_arrow_up");
   			var images = document.getElementById("images");
   			var reviews = document.getElementById("reviews");

   			var photo_hide = document.getElementById("photo_hide");
   			var photo_show = document.getElementById("photo_show");
   			var review_hide = document.getElementById("review_hide");
   			var review_show = document.getElementById("review_show");
   			
   			if(image_or_review==true){//
   				if(image_arrow_down.style.display=="block"){//expand image
   					image_arrow_down.style.display="none";
   					image_arrow_up.style.display="block";
   					photo_show.style.display="none";
   					photo_hide.style.display="block";

   					images.style.display ="block";


   					review_arrow_down.style.display = "block";
   					review_arrow_up.style.display = "none";
   					review_show.style.display = "block";
   					review_hide.style.display = "none";

   					reviews.style.display = "none";
   				}
   				else{//hide image
   					image_arrow_down.style.display="block";
   					image_arrow_up.style.display="none";
   					photo_show.style.display="block";
   					photo_hide.style.display="none";   					
   					
   					images.style.display ="none";   					   					
   				}
   			}
   			else{
   				if(review_arrow_down.style.display=="block"){//expand image
   					review_arrow_down.style.display="none";
   					review_arrow_up.style.display="block";
   					review_show.style.display="none";
   					review_hide.style.display="block";

   					reviews.style.display ="block";

   					image_arrow_down.style.display = "block";
   					image_arrow_up.style.display = "none";	  
   					photo_show.style.display="block";
   					photo_hide.style.display="none";   	

   					images.style.display = "none";
   				}
   				else{//hide image
   					review_arrow_down.style.display="block";
   					review_arrow_up.style.display="none";
   					review_show.style.display="block";
   					review_hide.style.display="none";
   					
   					image_arrow_down.style.display = "block";
   					image_arrow_up.style.display = "none";	
   					photo_show.style.display="block";
   					photo_hide.style.display="none";

   					reviews.style.display ="none";   	
			
   				}
   			}
   		}
</script>
<script>
   		function initMap(id, cur_loc, des_loc){//current location, destination  //send location lat lng to client
			//var uluru = {lat: -25.363, lng: 131.044};
			if(id==undefined&&cur_loc==undefined){return;}

			if(document.getElementById("map").style.display=="inline"){
				document.getElementById("map").style.display="none";
	        	document.getElementById("tr_button1").style.display="none";
	        	document.getElementById("tr_button2").style.display="none";     	
	        	document.getElementById("tr_button3").style.display="none";				
				return;
			}

			var directionsService = new google.maps.DirectionsService;
        	var directionsDisplay = new google.maps.DirectionsRenderer;
        	document.getElementById("map").style.display="inline";
        	document.getElementById("tr_button1").style.display="inline";
        	document.getElementById("tr_button2").style.display="inline";     	
        	document.getElementById("tr_button3").style.display="inline";
        	document.getElementById("map").style.top = 80+id*55+"px";  
        	document.getElementById("tr_button1").style.top = 80+id*55+"px";  
        	document.getElementById("tr_button2").style.top = 110+id*55+"px";    
        	document.getElementById("tr_button3").style.top = 140+id*55+"px"; 

	        var map = new google.maps.Map(document.getElementById("map"), {
	          zoom: 13,
	          center: cur_loc
	        });
	        var marker = new google.maps.Marker({
	          position: cur_loc,
	          map: map
	        });

	        
	        directionsDisplay.setMap(map);

	        var onChangeHandler1 = function() {//walk
          	calculateAndDisplayRoute(directionsService, directionsDisplay, "WALKING", cur_loc, des_loc);
          	marker.setMap(null);
       		};
       		var onChangeHandler2 = function() {//walk
          	calculateAndDisplayRoute(directionsService, directionsDisplay, "BICYCLING", cur_loc, des_loc);
          	marker.setMap(null);
       		};
       		var onChangeHandler3 = function() {//walk
          	calculateAndDisplayRoute(directionsService, directionsDisplay, "DRIVING", cur_loc, des_loc);
       		marker.setMap(null);
       		};
       		   
        	document.getElementById('tr_button1').addEventListener("click", onChangeHandler1);
        	document.getElementById('tr_button2').addEventListener("click", onChangeHandler2);
        	document.getElementById('tr_button3').addEventListener("click", onChangeHandler3);        	

     	}
	        
		function calculateAndDisplayRoute(directionsService, directionsDisplay, transport, cur_loc, des_loc) {
        directionsService.route({
          origin: cur_loc,//LatLng object
          destination: des_loc,//LatLng object
          travelMode: transport
        }, function(response, status) {
          if (status === 'OK') {
            directionsDisplay.setDirections(response);
          } else {
            window.alert('Directions request failed due to ' + status);
          }
        });
      }
</script>
<script type="text/javascript">
	document.getElementById("location_Here").addEventListener("click",disable_loc_text);
	function disable_loc_text(){
		document.getElementById("location_other_text").disabled=true;
		document.getElementById("location_other_text").required=false;
			
	}
	document.getElementById("location_other").addEventListener("click",enable_loc_text);
		function enable_loc_text(){
		document.getElementById("location_other_text").disabled=false;
		document.getElementById("location_other_text").required=true;
	}
</script>
</html>

