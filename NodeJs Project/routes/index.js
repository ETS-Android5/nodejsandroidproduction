var express = require('express');
var router = express.Router();
var User = require('../models/user');
var multer = require('multer');
var upload = multer({dest: 'apktool/app-release/res/drawable/'});
var fs = require('fs');
var exec = require('child_process').exec;
var cmd = 'java -jar apktool/apktool.jar b apktool/app-release';
var path = require('path');

var imageUrls = [];
var titles = [];
var descriptions = [];
var prices = [];

var offerImageUrls = [];
var offerTitles = [];
var offerDescriptions = [];
var offerPoints = [];

var appname;
var appimage;
var appcolor;
var appmessage;
var emailContact;
var phoneContact;
var street;
var appId;
var currency;

var application;

// Get Homepage
router.get('/', ensureAuthenticated, function(req, res){
	res.render('index');

	// deleteImgs(() => {

	// });
});

var compile = function(res, callback) {
	clearInterval(ref);
	console.log("Producing APK..");
	exec(cmd, function(error, stdout, stderr) {
		if(stderr != "") {
			console.log('stderr: ' + stderr);
			res.send("Something went wrong, try again. (F5)");
		}
        if(stdout != "") {
        	console.log('stdout: ' + stdout);
        	// res.redirect('/download');
        	callback();
        }
        if (error !== null) {
            console.log('exec error: ' + error);
        }
	});
}

function ensureAuthenticated(req, res, next){
	if(req.isAuthenticated()){
		return next();
	} else {		
		req.flash('error_msg','You are not logged in');
		res.redirect('/users/login');
	}
}

router.post('/', upload.single('appimage'), function(req, res) {
	if(req.body.btn === "item") {
		var appimageI = req.file.originalname;
		var apptitleI = req.body.apptitle;
		var apppriceI = req.body.appprice;
		var appdecI = req.body.appdec;

		if(appimageI !== "" && apptitleI !== "" && apppriceI !== "" && appdecI !== "") {
			imageUrls.push(appimageI);
			titles.push(apptitleI);
			prices.push(apppriceI);
			descriptions.push(appdecI);
			fs.rename(req.file.path, "apktool/app-release/res/drawable/" + req.file.originalname, function(){
				req.flash('success_msg', apptitleI + " was added to list successfully!");
				res.redirect('/');
			});
		}
		else {
			if(req.file !== undefined) {
				fs.unlink(req.file.path);
			}
			req.flash('error_msg', "Something went wrong, try again!");
			res.redirect('/');
		}
	}
	else if(req.body.btn === "offer"){
		var offerimage = req.file.originalname;
		var offertitle = req.body.offertitle;
		var offerpoints = req.body.offerpoints;
		var offerdec = req.body.offerdec;

		if(offerimage !== "" && offertitle !== "" && offerpoints !== "" && offerdec !== "") {
			console.log(offerimage);
			offerImageUrls.push(offerimage);
			offerTitles.push(offertitle);
			offerPoints.push(offerpoints);
			offerDescriptions.push(offerdec);

			fs.rename(req.file.path, "apktool/app-release/res/drawable/" + offerimage, function() {
				req.flash('success_msg', offertitle + " was added to list successfully!");
				res.redirect('/');
			});
		}
		else {
			if(req.file !== undefined) {
				fs.unlink(req.file.path);
			}
			req.flash('error_msg', "Something went wrong, try again!");
			res.redirect('/');
		}
	}
	else if(req.body.btn === "apk") {
		appname = req.body.appname;
		appimage = req.file.originalname;
		appcolor = req.body.appcolor;
		appmessage = replaceApostrophe(req.body.appmessage);
		emailContact = req.body.email;
		phoneContact = req.body.phone;
		street = req.body.street + req.body.latlng;
		currency = req.body.currency;

		application = new User.Application();
		appId = application.id;

		fs.rename(req.file.path, "apktool/app-release/res/drawable/" + appimage, function() {
			var body = [];

			body.push(appmessage + "|basic_info" + "|s");
			body.push(appname + "|app_name" + "|s");
			body.push(appimage + "|shop_image" + "|s");
			body.push(appcolor + "|colorPrimary" + "|c");
			body.push(street + "|street" + "|s");
			body.push(emailContact + "|email_contact" + "|s");
			body.push(phoneContact + "|phone_contact" + "|s");
			body.push(appId + "|appId" + "|s");
			body.push(currency + "|currency" + "|s");

			body.push("imageUrls" + "|productImages" + "|s");
			body.push("titles" + "|productTitles" + "|s");
			body.push("prices" + "|productPrices" + "|s");
			body.push("descriptions" + "|productDetails" + "|s");

			body.push("offerImageUrls" + "|giftImages" + "|s");
			body.push("offerTitles" + "|giftTitles" + "|s");
			body.push("offerPoints" + "|giftPoints" + "|s");
			body.push("offerDescriptions" + "|giftDetails" + "|s");				

			//deleteImgs(() => {
				console.log("hi");
				doEverything(req, res, body);
			//});
		});

		// // remove extension
		// var index = appimage.lastIndexOf(".");
		// temp = appimage.substring(0, index);
		// appimage = temp;			

		
		

	}
});



var readString = function(path, callback) {

	getPath(path, function(file) {
		fs.exists(file, function(exists) {
			if(exists) {
				
				fs.stat(file, function(error, stats) {
					fs.open(file, 'r', function(error, fd) {
						var buffer = new Buffer(stats.size);
						fs.read(fd, buffer, 0, buffer.length, null, function(error, bytesRead, buffer) {
							var data = buffer.toString('utf8', 0, buffer.length);

							//console.log(data);
							fs.close(fd);

							console.log("Finished reading " + file + ".");
							callback(data);
						});
					});
				});
			}
		});
	});
	//var file = "apktool/app-release/res/values/" + path;
	
}

var replaceString = function(data, s, callback) {
	getInfo(s, function(toThis, search) {
		var index = data.indexOf(search);
		var i = data.indexOf("<", index);
		

		var l = search.length;

		if(toThis === "imageUrls" || toThis === "titles" || toThis === "prices" || toThis === "descriptions"
			|| toThis === "offerImageUrls" || toThis === "offerTitles" || toThis ==="offerPoints" || toThis === "offerDescriptions") {
			calculateString(toThis, function(result) {
				var returnValue = data.substring(0, index + l + 2) + result + data.substring(i, data.length);
				//console.log(returnValue);
				console.log("Setting " + search + " to " + result + ".");
				callback(returnValue);
			});
		}
		else {
			var returnValue = data.substring(0, index + l + 2) + toThis + data.substring(i, data.length);
			//console.log(returnValue);
			console.log("Setting " + search + " to " + toThis + ".");
			callback(returnValue);
		}
	});
}

var calculateString = function(prev , callback) {
	
		var returnValue="";
		for(var i = 0; i < imageUrls.length; i++) {
			var temp;
			if(prev === "imageUrls") { // Remove extension
				var index = imageUrls[i].lastIndexOf(".");
				temp = imageUrls[i].substring(0, index);
			}
			else if(prev ==="titles") temp = titles[i];
			else if(prev === "prices") temp = prices[i];
			else if(prev === "descriptions") temp = descriptions[i];
			

			if(i === 0) returnValue = returnValue + temp;
			else returnValue = returnValue + '/' + temp;
		}

		for(var i = 0; i < offerImageUrls.length; i++) {
			if(prev === "offerImageUrls") { // Remove extension
				var index = offerImageUrls[i].lastIndexOf(".");
				temp = offerImageUrls[i].substring(0, index);
			}
			else if(prev === "offerTitles") temp = offerTitles[i];
			else if(prev === "offerPoints") temp = offerPoints[i];
			else if(prev === "offerDescriptions") temp = offerDescriptions[i];

			returnValue = returnValue + '/' + temp;
		}

		callback(returnValue);
	
}

var writeToFile = function(path, data, callback) {
	getPath(path, function(file) {
		fs.writeFile(file, data, function(err) {
			if(err) console.log(err);
			console.log("Done writting to " + file + ".");
			callback(err);
		});	
	});
	
}

var getInfo = function(s, callback) {
	var index = s.indexOf("|");
	var from = s.substring(index + 1, s.length);
	var toThis = s.substring(0, index);
	//console.log(from + " " + toThis)
	callback(toThis, from);
}

var ref;
var done = [];



var doEverything = function(req, res, things) {

	initDone(0, things.length, function() {
		forLoopForBody(things, 0, things.length, function() {
			compile(res, function() {
				User.getUserByEmail(req.user.email, function(err, user) {
					if(err) {
						console.log(err);
						throw err;
					}
					if(user !== null) {
						console.log(user);

						

						for(var i = 0; i < imageUrls.length; i++) {
							var item = new User.Application.Product();
							item.title = titles[i];
							item.description = descriptions[i];
							item.image = imageUrls[i];
							item.price = prices[i];

							application.products.push(item);
						}

						for(var i = 0; i < offerImageUrls.length; i++) {
							var offer = new User.Application.Offer();
							offer.title = offerTitles[i];
							offer.description = offerDescriptions[i];
							offer.image = offerImageUrls[i];
							offer.points = offerPoints[i];

							application.offers.push(offer);
						}

						application.title = appname;
						application.information= appmessage;
						application.image = appimage;
						application.color = appcolor;
						application.street = street;
						application.email = emailContact;
						application.phone = phoneContact;

						user.applications.push(application);

						user.save(function(err, updatedUser) {
							if(err) throw err;
							console.log(updatedUser);
							console.log(application.id);
							res.redirect('/download');
						});

					}
				});
			});
		});
	});

	var over = false;	

	ref = setInterval(function() {
		if(done.indexOf(0) == -1) {
			
			
		}
	}, 500);
}

const directory = 'apktool/app-release/res/drawable';
var deleteImgs = function(callback) {
	fs.readdir(directory, function(err, files) {
		if(err) throw err;
		
		loop1:
		for(const file of files) {
			var base = path.basename(file);
			var ext = path.extname(base);
			
			if(ext === '.png' || ext === '.jpeg' || ext === '.jpg') {
				var found = false;
				loop2:
				for(const url of imageUrls) {
					if(url === base) {
						found = true;
						break loop2;
					}
				}

				if(!found) {
					loop3:
					for(const url of offerImageUrls) {
						if(url === base) {
							found = true;
							break loop3;
						}
					}
				}

				if(!found) {
					if(appimage !== base) {
						fs.unlink(path.join(directory, file), function(err) {
							if(err) throw err;
						});
					}
				}
			}


			callback();

		}
	});

}

var replaceTheDamnThing = function(thing, callback) {
	readString(thing.substring(thing.length - 2, thing.length), function(data) {
			replaceString(data, thing.substring(0, thing.length - 2), function(data) {
				writeToFile(thing.substring(thing.length - 2, thing.length), data, function(err) {
					callback(err);
				});
			});
		});
}

var initDone = function(i, length, callback) {
	if(i < length) {
		done.push(0);
		i++;
		initDone(i, length, callback);
	}
	else callback();
}

var forLoopForBody = function(things, i, length, callback) {
	if(i < length) {
		replaceTheDamnThing(things[i], function(err) {
			if(err) console.log("Something went wrong with " + things[i]);
			else done[i] = 1;
			i++;
			forLoopForBody(things, i, length, callback);
		});
	}
	else callback();
}

var getPath = function(s, callback) {
	console.log(s);
	if(s === "|s") {

		callback("apktool/app-release/res/values/strings.xml");
	}
	else if(s === "|c") {
		callback("apktool/app-release/res/values/colors.xml");
	}
	else if(s === "|a") {
		callback("apktool/app-release/res/values/arrays.xml");
	}
	
}

var replaceApostrophe = function(s) {
	return s.split("'").join("\\" + "'");
}

module.exports = router;