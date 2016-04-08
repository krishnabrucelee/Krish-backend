<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
<title>
</title>
</head>
<body>
<div style="margin:0">
        <table width="100%" border="0" style="padding:24px;background-color:#34495e" cellspacing="0" cellpadding="0" align="center">
            <tbody><tr>
                <td>
                    <table width="540" border="0" align="center" cellpadding="0" cellspacing="0" style="padding:38px 30px 30px 30px;background-color:#fafafa">
                        <tbody><tr>
                            <td width="50%">

                                <a href="http://www.teamsun.com.cn/" target="_blank">

                                    <img src="http://www.teamsun.com.cn/en/style/index/images/logo.png" alt="Teamsun" title="Teamsun" class="CToWUd">
                                </a>
                            </td>
                            <td width="50%" style="text-align:right">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="10" style="border-bottom:1px solid #eaedef">

                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="24">

                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <table>
                                    <tbody><tr>
                                        <td>

                                            <img src="https://dmx246cm6p7k8.cloudfront.net/content/images/emailer/failure-icon.png" alt="" title="Your Recharge was Successful" class="CToWUd">
                                        </td>
                                        <td style="font-size:18px;font-family:Arial,Helvetica,sans-serif;color:#34495e">

                                                                                                                                               Your Invoice payment for <span>CNY. ${payment.totalFee}</span> is failure!
                                                                                                                                    </td>
                                    </tr>
                                </tbody></table>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="28">

                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" style="font-size:12px;font-family:Arial,Helvetica,sans-serif;color:#34495e">

                                Dear <b>${payment.domain.name}</b>,
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="10">

                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" style="font-size:12px;line-height:1.4;font-family:Arial,Helvetica,sans-serif;color:#34495e">
                                                                                                     Your Payment was unsuccessful.
                                                                                                 We hope you had a smooth and rewarding experience at <span class="il">Teamsun</span>. Below are your transaction details.
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="24">

                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <table width="100%" style="padding:18px 18px 14px 18px;border:1px solid #eaedef;background-color:#ffffff">
                                    <tbody><tr>
                                        <td width="54">
                                        </td>
                                        <td width="160" style="font-size:20px;font-family:Arial,Helvetica,sans-serif;color:#344960">

                                           ${payment.subject}

                                            <p style="margin:0;font-size:11px;font-family:Arial,Helvetica,sans-serif;color:#919bae">
                                                                                                   ${payment.domain.name}
                                                                                            </p>
                                        </td>
                                        <td style="text-align:center">

                                            <img src="https://dmx246cm6p7k8.cloudfront.net/content/images/emailer/plus-icon.png" alt="Plus" title="Plus" class="CToWUd">
                                        </td>
                                        <td width="70" style="text-align:center;font-size:20px;font-family:Arial,Helvetica,sans-serif;color:#344960">

                                                                                          0
                                                                                                                                    <p style="margin:0;font-size:11px;font-family:Arial,Helvetica,sans-serif;color:#919bae">Discounts</p>
                                        </td>
                                        <td style="text-align:center">

                                            <img src="https://dmx246cm6p7k8.cloudfront.net/content/images/emailer/arrow-icon.png" alt="Equals" title="Equals" class="CToWUd">
                                        </td>
                                        <td width="80" style="text-align:right;font-size:20px;font-family:Arial,Helvetica,sans-serif;color:#344960">

                                            <span style="font-size:11px;font-family:Arial,Helvetica,sans-serif;color:#919bae">CNY.</span>${payment.totalFee}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="6" height="10" style="border-bottom:1px solid #eaedef">

                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="6" height="8">

                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="3" style="font-size:12px;font-family:Arial,Helvetica,sans-serif;color:#344960">

                                            <span style="color:#919bae">Order ID : </span>${payment.orderId}
                                        </td>
                                        <td colspan="3" style="text-align:right;font-size:12px;font-family:Arial,Helvetica,sans-serif;color:#344960">

                                                                                                                                      <span style="color:#919bae">Date : </span>${payment.notifyTime}
                                                                                    </td>
                                    </tr>
                                </tbody></table>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="24">

                            </td>
	                        <tr>
	                            <td colspan="2" height="24">
                                        <div class="pull-right">Transaction Status  : <span>Failure</span></div>
	                            </td>
	                        </tr>
	                        <tr>
                            <td colspan="2" height="24">

                            </td>
                        </tr>
                                                <tr>
                            <td colspan="2" style="line-height:28px;font-size:16px;font-family:Arial,Helvetica,sans-serif;color:#34495e">
                                <a style="text-align:right;font-size:12px;font-family:Arial,Helvetica,sans-serif;color:#344960" href="${payment.url}">Check transaction history</a>
                            </td>
                        </tr>
                        <tr>
                        </tr>
                        <tr>
                            <td colspan="2" height="30" style="border-bottom:1px solid #eaedef">

                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" height="12">

                            </td>
                        </tr>
                        <tr>

                        </tr>
                        <tr>
                           <td colspan="2">
                                                         </td>
                        </tr>
                    </tbody></table>
                </td>
            </tr>
        </tbody></table>
              <div class="yj6qo"></div><div class="adL">
            </div></div>
        </div>
        </body>
</html>
