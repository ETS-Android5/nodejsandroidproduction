var express = require('express');
var router = express.Router();
var User = require('../models/user');

// Data to be sent
var data = [];

router.get('/', ensureAuthenticated, function(req, res){

	User.getUserByEmail(req.user.email, function(err, user) {
		if(err) throw err;
		if(user) {
			data = [];
			constructData(user, function() {
				res.render('statistics', {data: data});
			});
		}
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

function constructData(user, callback) {
	
	for(var i = 0; i < user.applications.length; i++) {
		getAgeCategories(user.applications[i].users, function(ageCategories) {
			getDaysAvg(user.applications[i].users, function(daysAvg) {
				getCheckInsLastWeek(user.applications[i].users, function(labels, checkIns) {
					getGenders(user.applications[i].users, function(genders) {
						data.push({
							title: user.applications[i].title,
							ageCategories: ageCategories,
							ageId: "age" + i,
							daysAvg: daysAvg,
							avgId: "avg" + i,
							weekLabels: labels,
							weekData: checkIns,
							weekId: "week" + i,
							genders: genders,
							genderId: "gender" + i
						});
					});					
				});				
			});
		});
	}
		
	callback();
	
}

function getGenders(users, callback) {
	var genders = [0, 0, 0];
	for(var i = 0; i < users.length; i++) {
		if(users[i].gender === "male") genders[0]++;
		else if(users[i].gender === "female") genders[1]++;
		else genders[2]++;
	}

	callback(genders);
}

function getCheckInsLastWeek(users, callback) {
	var labels = [];
	var checkIns = [0, 0, 0, 0, 0, 0, 0];
	for(var i = 6; i >= 0; i--) {
		var d = new Date();
		d.setDate(d.getDate() - i);
		labels.push( {
			value: getStrFromDay(d.getDay()) + " " + d.getUTCDate() + "/" + (d.getMonth() + 1)
		});

		for(var j = 0; j < users.length; j++) {
			for(var k = 0; k < users[j].timesCheckedIn.length; k++) {
				var date = users[j].timesCheckedIn[k];
				if(date.getDate() === d.getDate() && date.getMonth() === d.getMonth() && date.getFullYear() === d.getFullYear()) {
					checkIns[6-i]++;
				}
			}
		}
	}

	callback(labels, checkIns);

}

function getStrFromDay(day) {
	var str;

	switch (day) {
	    case 0:
	        str = "Sun";
	        break;
	    case 1:
	        str = "Mon";
	        break;
	    case 2:
	        str = "Tue";
	        break;
	    case 3:
	        str = "Wed";
	        break;
	    case 4:
	        str = "Thu";
	        break;
	    case 5:
	        str = "Fri";
	        break;
	    case 6:
	        str = "Sat";
	        break;
	}

	return str;
}

function getDaysAvg(users, callback) {
	var days = [0, 0, 0, 0, 0, 0, 0];
	var checkIns = [0, 0, 0, 0, 0, 0, 0];

	for(var i = 0; i < 30; i++) {
		var date = new Date();
		date.setDate(date.getDate() - i);
		var day = date.getDay();
		days[day]++;
		for(var j = 0; j < users.length; j++) {
			for(var k = 0; k < users[j].timesCheckedIn.length; k++) {
				var d = users[j].timesCheckedIn[k];
				if(date.getDate() === d.getDate() && date.getMonth() === d.getMonth() && date.getFullYear() === d.getFullYear()) {
					checkIns[day]++;
				}
			}
		}
	}

	var avgs = [];
	for(var i = 0; i < 7; i++) {
		avgs.push(checkIns[i] / days[i]);
	}

	callback(avgs);
}

function getAgeCategories(users, callback) {
	var age12_less = 0;
	var age13_17 = 0;
	var age18_24 = 0;
	var age25_34 = 0;
	var age35_44 = 0;
	var age45_54 = 0;
	var age55_64 = 0;
	var age65_above = 0;
	var unknown = 0;

	var now = new Date();

	for(var i = 0; i < users.length; i++) {
		var bday = users[i].birthday;
		if(bday === "undefined") unknown++;
		else {
			var parts = bday.split('-');
			var date = new Date(parts[0], parts[1] - 1, parts[2]);
			var age = now.getFullYear() - date.getFullYear();
			var m = now.getMonth() - date.getMonth();
			if(m < 0 || (m === 0 && now.getDate() < date.getDate())) {
				age--;
			}

			if(age <= 12) age12_less++;
			else if(age <= 17) age13_17++;
			else if(age <= 24) age18_24++;
			else if(age <= 34) age25_34++;
			else if(age <= 44) age35_44++;
			else if(age <= 54) age45_54++;
			else if(age <= 64) age55_64++;
			else age65_above++;
		}
	}



	callback([age12_less, age13_17, age18_24, age25_34, age35_44, age45_54, age55_64, age65_above, unknown]);
}

module.exports = router;