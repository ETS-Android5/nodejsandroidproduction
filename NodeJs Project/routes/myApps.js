var express = require('express');
var router = express.Router();
var User = require('../models/user');

router.get('/', ensureAuthenticated, function(req, res) {
	
	User.getUserByEmail(req.user.email, function(err, user){
		if(err) throw err;
		res.render('myApps', {apps: user.applications});

		// var dummyData = [];
		// for(var i = 0; i < 100; i++) {
		// 	var dummy = new User.Application.AuthUser();
		// 	dummy.email = "dummy@email.com";
		// 	var year = Math.floor(Math.random() * 80 + 1938);
		// 	var month = Math.floor(Math.random() * 12);
		// 	var day = Math.floor(Math.random() * 30);
		// 	dummy.birthday = year + "-" + month + "-" + day
		// 	var r = Math.floor(Math.random() * 4);
		// 	if(r === 0) dummy.gender = "male";
		// 	else if(r === 1) dummy.gender = "female";
		// 	else if(r === 2) {
		// 		dummy.gender = "undefined";
		// 	}
		// 	else if(r===3) {
		// 		dummy.gender = "male";
		// 		dummy.birthday = "undefined";
		// 	}
		// 	var length = Math.floor(Math.random() * 30);
		// 	var k = -1;
		// 	for(var j = 0; j < length; j++) {
		// 		k += Math.floor(Math.random() * 3 + 1);
		// 		var date = new Date();
		// 		date.setDate(date.getDate() - k);
		// 		dummy.timesCheckedIn.push(date);
		// 	}



			

		// dummyData.push(dummy);

		//  console.log("Index: " + i + '\n' + dummy);
		// console.log(dummy.timesCheckedIn);
		// // 	//console.log(year + " " + month + " " + day);
		// }





		// User.getApplicationById( user.id,user.applications[0].id, function(t, index) {
		// 	// if(t) {
		// 	// 	// for(var i = 0; i < user.applications[index].users.length; i++) {
		// 	// 	// 	console.log("Index: " + i + '\n' + user.applications[index].users[i]);
		// 	// 	// }
		// 	// 	user.applications[index].users = [];

		// 	// 	user.save(function(err, updatedUser) {
		// 	// 		console.log(updatedUser.applications[index].users.length);
		// 	// 	});
		// 	// }

		// 	user.applications[index].users = dummyData;

		// 		user.save(function(err, updatedUser) {
		// 			if(err) {
		// 				console.log(err);
		// 				throw err;
		// 			}
		// 			if(updatedUser) {
		// 				console.log(updatedUser.applications[index].users.length);
		// 			}
		// 			else {
		// 				console.log("nope");
		// 			}
					
		// 		});

		// 	// for(var i = 0; i <user.applications[index].users.length; i++) {
		// 	// 	var d = user.applications[index].users[i].birthday;

		// 	// 	console.log(d.getUTCFullYear() + "/" + (d.getUTCMonth() + 1) + "/" + d.getUTCDay());
		// 	// }

			
		// });
	});
});

function ensureAuthenticated(req, res, next){
	if(req.isAuthenticated()){
		return next();
	} else {		
		req.flash('error_msg','You are not logged in');
		res.redirect('/users/login');
	}
}

module.exports = router;