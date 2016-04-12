package ck.panda.payment.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ck.panda.constants.PaymentConstants;
import ck.panda.domain.entity.PaymentGateway;
import ck.panda.service.PaymentGatewayService;

/**
 * Class name : AlipayNotify Function: Alipay notification processing class Details: Alipay each interface processing
 * returns notice Version: 3.3 Date: 2012-08-17 Description : The following code is sample code to test for the
 * convenience of business and provided businesses as needed for your site, according to technical writing , not have to
 * use the code. The code for study and research Alipay interface , only provide a reference
 */
@Component
public class AlipayNotify {

    /** Payment gateway reference. */
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    /**
     * Alipay address Message Authentication
     */
    private static final String HTTPS_VERIFY_URL = "https://www.alipay.com/cooperate/gateway.do?service=notify_verify&";

    /**
     * Verify that the message is sent treasure to pay legitimate messages.
     *
     * @param Params notice to return an array of parameters
     * @return Validation results
     */
    public boolean verify(Map<String, String> params) {
        // Determine whether responsetTxt true, isSign whether true
        // ResponsetTxt results are not true, and the server setup problem ,
        // cooperative identity provider ID, ​​notify_id one minute about the
        // failure
        // IsSign not true, and Security Code , in the format of the request (
        // eg : with custom parameters, etc. ) , related to the encoding format
        String responseTxt = "false";
        if (params.get("notify_id") != null) {
            String notify_id = params.get("notify_id");
            responseTxt = verifyResponse(notify_id);
        }
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
        }
        boolean isSign = getSignVeryfy(params, sign);
        // Write log records ( To debug , uncheck the following two lines of
        // comments )
        // String sWord = "responseTxt =" + responseTxt + "\ n isSign =" +
        // isSign + "\ n return back parameters :" + AlipayCore.createLinkString
        // (params);
        // AlipayCore.logResult(sWord);

        if (isSign && responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Based on the information fed back to generate signature result
     *
     * @param Params notification returned to the array of arguments
     * @param Sign signature result comparison of
     * @return Generated signature result
     */
    private boolean getSignVeryfy(Map<String, String> Params, String sign) {
        // Filter nulls , sign and sign_type parameters
        Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
        String preSignStr = AlipayCore.createLinkString(sParaNew);
        boolean isSign = false;
        PaymentGateway paymentGateway = paymentGatewayService.getActivePaymentGateway(true);
        if (PaymentConstants.SIGN_TYPE.equals("MD5")) {
            isSign = ck.panda.payment.MD5.verify(preSignStr, sign, paymentGateway.getSecurityCode(),
                    PaymentConstants.INPUT_CHARSET);
        }
        return isSign;
    }

    /**
     * Get the remote server ATN results verify the return URL
     *
     * @param Notify_id notification check ID
     * @return Server ATN results Verify the result set : Invalid command parameter does not appear this error , please
     *         check the return process and key partner is empty True returns the correct information False Check the
     *         firewall or the server port to prevent problems and to verify whether the time over one minute
     */
    private String verifyResponse(String notify_id) {
        // Get a remote server ATN results verify that Alipay is sent from the
        // server request
        PaymentGateway paymentGateway = paymentGatewayService.getActivePaymentGateway(true);
        String partner = paymentGateway.getPartner();
        String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;

        return checkUrl(veryfy_url);
    }

    /**
     * Get the remote server ATN results
     *
     * @param Urlvalue path specified URL address
     * @return Server ATN results Verify the result set : Invalid command parameter does not appear this error , please
     *         check the return process and key partner is empty True returns the correct information False Check the
     *         firewall or the server port to prevent problems and to verify whether the time over one minute
     */
    private String checkUrl(String urlvalue) {
        String inputLine = "";

        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            inputLine = in.readLine().toString();
        } catch (Exception e) {
            e.printStackTrace();
            inputLine = "";
        }

        return inputLine;
    }
}
