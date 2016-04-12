<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html data-ng-app="homer">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Page title set in pageTitle directive -->
    <title page-title></title>
    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->
    <link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />

    <!-- build:css(.) styles/vendor.css -->
    <!-- bower:css -->
    <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.css" />

    <!-- endbower -->
    <!-- endbuild -->

    <!-- build:css({.tmp,app}) styles/style.css -->
    <link rel="stylesheet" href="styles/style.css">
    <!-- endbuild -->

</head>
<body class="blank" data-ng-controller="loginCtrl">


<div class="login-container">
    <div class="row">
        <div class="col-md-12">
            <div class="text-center m-b-md">
                <h4 class="font-bold text-primary">Payment Confirm</h4>
                <small>Cloud Management Portal</small>
            </div>
            <div class="hpanel hbgblue">
                <div class="panel-body" data-ng-controller= "paymentController">
                  <div data-ng-bind-html="data"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 text-center">
        Panda Cloud Management Portal<br/> Copyright © 2015 BlueTek Co.Ltd, All rights reserved.
        </div>
    </div>
</div>


</body>

<script src="bower_components/jquery/dist/jquery.min.js"></script>
<script src="bower_components/angular/angular.min.js"></script>
<script src="scripts/controllers/paymentController.js"></script>
<script src="scripts/factories/rememberMeService.js"></script>
<script src="scripts/factories/globalConfig.js"></script>
</html>