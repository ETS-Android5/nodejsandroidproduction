var mongoose = require('mongoose');
var bcrypt = require('bcryptjs');

// Product Item Schema
var ProductSchema = mongoose.Schema({
	title: String,
	description: String,
	image: String,
	price: Number
}, {
  usePushEach: true
});

// Offer Item Schema
var OfferSchema = mongoose.Schema({
	title: String,
	description: String,
	image: String,
	points: Number
}, {
  usePushEach: true
});

// Authenticated User Schema
var AuthenticatedUserSchema = mongoose.Schema({
	email: String,
	points: {
		type: Number,
		default: 0
	},
	lastTimeCheckedIn: Date,
	timesCheckedIn: [Date],
	gender: String,
	birthday: String
}, {
  usePushEach: true
});

// Code Schema
var CodeSchema = mongoose.Schema({
	value: String,
	email: String,
	date: {
		type: Date,
		default: Date.now
	}
}, {
  usePushEach: true
});

// Application Schema
var ApplicationSchema = mongoose.Schema({
	title: String,
	information: String,
	image: String,
	color: String,
	street: String,
	email: String,
	phone: String,
	products: [ProductSchema],
	offers: [OfferSchema],
	users: [AuthenticatedUserSchema]
}, {
  usePushEach: true
});

// User Schema
var UserSchema = mongoose.Schema({
	username: {
		type: String
	},
	password: {
		type: String
	},
	email: {
		type: String,
		index: true
	},
	name: {
		type: String
	},
	applications: [ApplicationSchema],
	codes: [CodeSchema]
}, {
  usePushEach: true
});

var User = module.exports = mongoose.model('User', UserSchema);
User.Application = mongoose.model('Application', ApplicationSchema);
User.Application.Product = mongoose.model('Product', ProductSchema);
User.Application.Offer = mongoose.model('Offer', OfferSchema);
User.Application.AuthUser = mongoose.model('AuthUser', AuthenticatedUserSchema);
User.Code = mongoose.model('Code', CodeSchema);

module.exports.ensureEmailUnique = function(email, callback) {
	User.findOne({email: email}, function(err, user) {
		if(err) callback(0, err);
		else if(user) callback(1, user);
		else callback(2, email);
	});
}

module.exports.createUser = function(newUser, callback){
	bcrypt.genSalt(10, function(err, salt) {
	    bcrypt.hash(newUser.password, salt, function(err, hash) {
	        newUser.password = hash;
	        newUser.save(callback);
	    });
	});
}

module.exports.registerApp = function(appname, req, callback) {
	var query = {'username': req.user.username};
	User.findOneAndUpdate(query, {nameOfApp: appname}, {upsert:true}, callback);
}

module.exports.updateApp = function(color, message, req, callback) {
	var query = {'username': req.user.username};
	User.findOneAndUpdate(query, {colorOfApp: color, messageOfApp: message}, {upsert:true}, callback);
}

module.exports.getUserByUsername = function(username, callback){
	var query = {username: username};
	User.findOne(query, callback);
}

module.exports.getUserByEmail = function(email, callback){
	User.findOne({email: email}, callback);
}

module.exports.getUserById = function(id, callback){
	User.findById(id, callback);
}

module.exports.getApplicationById = function(userId, appId, callback) {
	User.getUserById(userId, function(err, user) {
		if(err) throw err;
		if(user) {
			for(var i = 0; i < user.applications.length; i++) {
				if(user.applications[i].id === appId) {
					callback(user, i);
					break;
				}
			}
		}
	});
}

module.exports.getUserByEmailFromThisApp = function(email, appId, userId, callback) {
	User.getApplicationById(userId, appId, function(user, index) {
		if(user) {
			for(var i = 0; i < user.applications[index].users.length; i++) {
				if(user.applications[index].users[i].email === email) {
					callback(user, index, user.applications[index].users[i]);
					return;
				}
			}
			callback(user, index, null);
		}
		else {
			callback(null, null, null);
		}
	});
	
}

module.exports.comparePassword = function(candidatePassword, hash, callback){
	bcrypt.compare(candidatePassword, hash, function(err, isMatch) {
    	if(err) throw err;
    	callback(null, isMatch);
	});
}