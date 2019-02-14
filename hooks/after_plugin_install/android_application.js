#!/usr/bin/env node


  var fs = context.requireCordovaModule('fs'),
    path = context.requireCordovaModule('path');

  var platformRoot = path.join(context.opts.projectRoot, 'platforms/android');
  var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');
  console.log("afterPluginInstallHook Called");



   console.log(manifestFile);
  if (fs.existsSync(manifestFile)) {

	   console.log("进入manifestFile了");


    fs.readFile(manifestFile, 'utf8', function (err,data) {
      if (err) {
        throw new Error('Unable to find AndroidManifest.xml: ' + err);
      }

      var appClass = 'com.zhaoyin.analytics.JBApplication';

      if (data.indexOf(appClass) == -1) {

        var result = data.replace(/<application/g, '<application android:name="' + appClass + '"');

        fs.writeFile(manifestFile, result, 'utf8', function (err) {
          if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
        })
      }
    });
  }