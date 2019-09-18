var express = require('express');
var router = express.Router();
var User = require('../models/user');
var mongoose = require('mongoose');

const {OAuth2Client} = require('google-auth-library');
const client_id = 'clientid_here';
const client = new OAuth2Client(client_id);

router.post('/addPoints', function(req, res) {
	var email = req.query.email;
	var appId = req.query.appId;
	var userId = req.query.userId;
	var points = req.query.points;

	console.log("Add points| Email: " + email + ", Points: " + points);

	User.getUserByEmailFromThisApp(email, appId, userId, function(user, index, foundUser) {
		if(user && foundUser) {
			var now = new Date();

			var shouldSave = false;

			if(foundUser.timesCheckedIn.length === 0) { // First check
				foundUser.lastTimeCheckedIn = now;
				foundUser.timesCheckedIn.push(now);
				foundUser.points = eval(foundUser.points) + eval(points);
				shouldSave = true;
			}
			else if(foundUser.timesCheckedIn.length > 0 ) { // A day passed
				if(now.getTime() - foundUser.lastTimeCheckedIn.getTime() >= 10000) {
					foundUser.lastTimeCheckedIn = now;
					foundUser.timesCheckedIn.push(now);
					foundUser.points = eval(foundUser.points) + eval(points);
					shouldSave = true;
				}
				else {
					res.send("early/0");
				}

				console.log(foundUser.points);
			}

			if(shouldSave) {
				user.save(function(err, updatedUser) {
					if(err) throw err;
					if(updatedUser) res.send("success/" + foundUser.points);
					else res.send("fail/0");
				});
			}
			
		}
		else {
			Console.log("Null users");
			res.send("fail/0");
		}
	});


});


router.get('/getPoints', function(req, res) {
	var email = req.query.email;
	var appId = req.query.appId;
	var userId = req.query.userId;

	console.log("Get points| Email: " + email);

	User.getUserByEmailFromThisApp(email, appId, userId, function(user, index, foundUser) {
		
		if(!foundUser) {
			console.log("Didn't find authenticated user of app.");
			res.send("fail/0");
		}
		else {
			res.send("success/" + foundUser.points);
		}
		
	});
});

router.post('/codeCreation', function(req, res) {
	var codeStr = req.query.code;
	var email = req.query.email;
	var appId = req.query.appId;
	var userId = req.query.userId;
	var points = req.query.points;

	console.log("New Code: " + codeStr + ", Email: " + email);

	User.getUserByEmailFromThisApp(email, appId, userId, function(user, index, foundUser) {
		if(user) {
				if(!foundUser) {
					console.log("Didn't find authenticated user of app.");
					res.send("fail/0");
				}
				else {
					var code = new User.Code();
					code.value = codeStr;
					code.email = email;
					user.codes.push(code);

					console.log("User points| Before: " + foundUser.points);

					foundUser.points -= points;

					console.log("User points| After: " + foundUser.points);


					user.save(function(err, updatedUser) {
						if(err) throw err;
						if(updatedUser) {
							User.Application.AuthUser.findOne({email: email}, function(err, refoundUser) {
								if(err) throw err;
								if(refoundUser) console.log(refoundUser.points);
							});
							res.send("success/" + foundUser.points);
						}
					});
				}
			
		}
		else {
			console.log("fail")
			res.send("fail/0");
		}
	});
});


router.post('/tokenAuthentication', function(req, res) {
	var tokenId = req.query.tokenId;
	var email = req.query.email;
	var appId = req.query.appId;
	var userId = req.query.userId;
	var gender = req.query.gender;
	var birthday = req.query.birthday;
	

	console.log("Email: " + email + ", AppId: " + appId + ", Gender: " + gender + ", Birthday: " + birthday);

	verify(tokenId, function(err) {
		if(err) {
			console.log(err);
			console.log("illegal");
			res.send("illegal");
		}
		else {
			User.getUserByEmailFromThisApp(email, appId, userId, function(user, index, foundUser ) {
				if(!foundUser) {
					console.log("USER NOT EXIST");
					var authUser = new User.Application.AuthUser();
					authUser.email = email;
					authUser.gender = gender;
					authUser.birthday = birthday;
					user.applications[index].users.push(authUser);

					user.save(function(err, updatedUser) {
						if(err) throw err;
						if(updatedUser) {
							var length = updatedUser.applications[index].users.length;
							console.log(length);
							console.log(updatedUser.applications[index].users[length - 1]);
							res.send("legal");
						}
						else {
							console.log("did not save");
						}
					});
				}
				else {
					console.log("USER ALREADY EXIST");
					res.send("legal");
				}
					
			});

			
		}
	});

});

async function verify(token, callback) {
	var error;

	try {
		const ticket = await client.verifyIdToken({
			idToken: token,
			audience: client_id
		});
		const payload = ticket.getPayload();
		const userid = payload['sub'];
	}
	catch(err) {
		error = err;
	}

	callback(error);
}


module.exports = router;