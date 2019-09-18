var express = require('express');
var router = express.Router();
var path = require('path');
var mime = require('mime');
var fs = require('fs');

router.get('/', ensureAuthenticated, function(req, res){
	if(req.user.nameOfApp === "" || req.user.nameOfApp === undefined) {
		res.redirect('/registerApp');
	}
	else {
		//res.render('download');
		//var file = __dirname + '/../apktool/app-release/dist/app-release.apk';
		var file = __dirname + '/../apktool/app-release/dist/app-release.apk';
  		//res.download(file);
  		var filename = path.basename(file);
  		var mimetype = mime.lookup(file);

 		 res.setHeader('Content-disposition', 'attachment; filename=' + filename);
  		res.setHeader('Content-type', mimetype);

  		var filestream = fs.createReadStream(file);
  		filestream.pipe(res);
	}
	
});

var download = function(url, dest, cb) {
  var file = fs.createWriteStream(dest);
  var request = http.get(url, function(response) {
    response.pipe(file);
    file.on('finish', function() {
      file.close(cb);  // close() is async, call cb after close completes.
    });
  }).on('error', function(err) { // Handle errors
    fs.unlink(dest); // Delete the file async. (But we don't check the result)
    if (cb) cb(err.message);
  });
};

function ensureAuthenticated(req, res, next){
	if(req.isAuthenticated()){
		return next();
	} else {		req.flash('error_msg','You are not logged in');
		res.redirect('/users/login');
	}
}

module.exports = router;