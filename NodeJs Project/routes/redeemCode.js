var express = require('express');
var router = express.Router();
var User = require('../models/user');

router.get('/', ensureAuthenticated, function(req, res){

	res.render('redeemCode');
	
});


router.post('/', function(req, res) {
	var email = req.body.email;
	var code = req.body.code;

	console.log("Redeem code| email: " + email + ", code: " + code);

	User.getUserByEmail(req.user.email, function(err, user) {
		if(err) throw err;
		if(user) {
			var index = -1;
			for(var i = 0; i < user.codes.length; i++) {
				if(user.codes[i].value === code && user.codes[i].email === email) {
					index = i;
					break;
					

					// if((Date.now() - user.codes[i].date) >= 64000) {
					// 	user.codes.splice(i, 1);
					// 	user.save(function(err, updatedUser) {
					// 		if(err) throw err;
					// 		if(updatedUser) {
					// 			req.flash('error_msg', 'The code has expired.');
					// 			res.redirect('/redeemCode');
					// 			return;
					// 		}
					// 		else {
					// 			req.flash('error_msg', 'Something went wrong. Please try again.');
					// 			res.redirect('/redeemCode');
					// 			return;
					// 		}
					// 	});
					// }
					// else {
					// 	user.save(function(err, updatedUser) {
					// 		if(err) throw err;
					// 		if(updatedUser) {
					// 			req.flash('success_msg', 'The code is valid.');
					// 			res.redirect('/redeemCode');
					// 			return;
					// 		}
					// 		else {
					// 			req.flash('error_msg', 'Something went wrong. Please try again.');
					// 			res.redirect('/redeemCode');
					// 			return;
					// 		}
					// 	});
					// }
					
				}
			}
			
			if(index !== -1) {
				var codeDate = user.codes[index].date;
				user.codes.splice(index, 1);
				
				user.save(function(err, updatedUser) {
					if(err) throw err;
					if(updatedUser) {
						if((Date.now() - codeDate) >= 64000) {
							req.flash('error_msg', 'The code has expired');
							res.redirect('/redeemCode');	
						}
						else {
							req.flash('success_msg', 'The code is valid.');
							res.redirect('/redeemCode');	
						}
					}
					else {
						req.flash('error_msg', 'Something went wrong. Please try again.');
						res.redirect('/redeemCode');	
					}
				});
			}
			else {
				req.flash('error_msg', 'The code was not found.');
				res.redirect('/redeemCode');			}
			
		}
		else {
			req.flash('error_msg', 'Something went wrong. Please try again.');
			res.redirect('/redeemCode');
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

module.exports = router;