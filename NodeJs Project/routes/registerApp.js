var express = require('express');
var router = express.Router();
var User = require('../models/user');

// Get Homepage
router.get('/', ensureAuthenticated, function(req, res){
	res.render('registerApp');
});

router.post('/', function(req, res) {
	var appname = req.body.appname;
	if(appname !== "") {
		User.registerApp(appname, req, function(err, doc) {
			if (err) {
				throw err;
				req.flash('error_msg', 'Something went wrong, try again!');
			}
			else {
				req.flash('success_msg', appname + " was registered successfully");
				res.redirect('/');
			}
			
		});
	}
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