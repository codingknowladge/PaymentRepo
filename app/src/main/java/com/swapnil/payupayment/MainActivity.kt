package com.swapnil.payupayment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebView
import android.widget.Button
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener

class MainActivity : AppCompatActivity() {
    private val surl = "https://payu.herokuapp.com/success"
    private val furl = "https://payu.herokuapp.com/failure"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var paybtn=findViewById<Button>(R.id.payubtn)
        paybtn.setOnClickListener {
paymentSetup()
        }
    }

    fun paymentSetup(){
        val additionalParamsMap: HashMap<String, Any?> = HashMap()
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF1] = "udf1"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF2] = "udf2"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF3] = "udf3"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF4] = "udf4"
        additionalParamsMap[PayUCheckoutProConstants.CP_UDF5] = "udf5"
        additionalParamsMap[PayUCheckoutProConstants.SODEXO_SOURCE_ID] = "srcid123"

        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount("1.0")
            .setIsProduction(true)
            .setKey("your key")
            .setProductInfo("Macbook Pro")
            .setPhone("8888888888")
            .setTransactionId(System.currentTimeMillis().toString())
            .setFirstName("John")
            .setEmail("john@yopmail.com")
            .setSurl(surl)
                .setFurl(furl)
            .build()

        PayUCheckoutPro.open(
            this, payUPaymentParams,
            object : PayUCheckoutProListener {


                override fun onPaymentSuccess(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                }


                override fun onPaymentFailure(response: Any) {
                    response as HashMap<*, *>
                    val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                    val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                }


                override fun onPaymentCancel(isTxnInitiated:Boolean) {
                }


                override fun onError(errorResponse: ErrorResponse) {
                    val errorMessage: String
                    if (errorResponse != null && errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                        errorMessage = errorResponse.errorMessage!!

//                        errorMessage = resources.getString(R.string.some_error_occurred)
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    //For setting webview properties, if any. Check Customized Integration section for more details on this
                }

                override fun generateHash(
                    valueMap: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {
                    if ( valueMap.containsKey(CP_HASH_STRING)
                        && valueMap.containsKey(CP_HASH_STRING) != null
                        && valueMap.containsKey(CP_HASH_NAME)
                        && valueMap.containsKey(CP_HASH_NAME) != null) {

                        val hashData = valueMap[CP_HASH_STRING]
                        val hashName = valueMap[CP_HASH_NAME]

                        //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                        val hash: String? = HashGenerationUtils.generateHashFromSDK(
                            hashData.toString(),
                            "your key"
                        )
                        if (!TextUtils.isEmpty(hash)) {
                            val dataMap: HashMap<String, String?> = HashMap()
                            dataMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(dataMap)
                        }
                    }
                }
            })
    }
}