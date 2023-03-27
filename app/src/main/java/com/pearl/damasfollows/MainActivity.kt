package com.pearl.damasfollows

import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var netwokChangeReciever: NetwokChangeReciever
    var ERP_Name = "damasfollows"
    var message = "message"
    var response = "response"
    var app_link = ""
    var name = ""
    var app_name = "damasfollows"
    var old_version_code = 0
    private val FILECHOOSER_RESULTCODE = 1
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mUploadMessages: ValueCallback<Array<Uri>>? = null
    private var mCapturedImageURI: Uri? = null

    val bottomNav:BottomNavigationView
    get() = findViewById(R.id.navigation_bar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#440f44")
        netwokChangeReciever = NetwokChangeReciever(this)
        val manager = this.packageManager
        try {
            val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
            old_version_code = info.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        name = intent.getStringExtra("user").toString()
        getDetails()

        try_again.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);
        bottomNavClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                progressBar.visibility = View.VISIBLE
                getDetails()
            }
            R.id.menu_exit -> {
                exitOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun exitOperation() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            val alertDialog2 = AlertDialog.Builder(
                this
            )
            alertDialog2.setTitle("Alert...")
            alertDialog2.setMessage("Are you sure you want to exit ?")


            alertDialog2.setPositiveButton(
                "Yes"
            ) { dialog: DialogInterface?, which: Int ->
                finishAffinity()
                System.exit(0)
            }
            alertDialog2.setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
            val dialog: AlertDialog = alertDialog2.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val wmlp = dialog.window!!.attributes
            wmlp.gravity = Gravity.BOTTOM
            alertDialog2.show()
            //       finish();
        }
    }


    private fun getDetails() {
        progressBar.setVisibility(View.VISIBLE)

        val FinalUrl = "https://development.pearl-developer.com/webapps/welcome/app_info"
        Log.i("MainActivity >>", "getDetails >> $FinalUrl")
        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, FinalUrl, { response ->
                val `object`: JSONObject
                try {
                    `object` = JSONObject(response)
                    val status = `object`.getString("response")
                    Log.i("MainActivity >> ", "getDetails - response >> $response")
                    if (status == "1") {
                        progressBar.setVisibility(View.VISIBLE)
                        val dataM = `object`.getJSONArray(message)
                        for (i in 0 until dataM.length()) {
                            val rec = dataM.getJSONObject(i)
                            app_link = rec.getString("app_link")
                            callWebView(app_link, progressBar)
                            val app_url_api = rec.getString("app_url")
                            val app_version_code = rec.getString("app_version_code").toInt()
                            val app_version_name = rec.getString("app_version_name")
                            if (old_version_code < app_version_code) {
                                val alertDialog2 = AlertDialog.Builder(
                                    this
                                )
                                alertDialog2.setTitle("Update Available")
                                alertDialog2.setMessage("New Version : $app_version_name")
                                alertDialog2.setPositiveButton(
                                    "Yes"
                                ) { dialog: DialogInterface?, which: Int ->
                                    startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(app_url_api))
                                    )
                                }
                                alertDialog2.setNegativeButton(
                                    "Skip"
                                ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
                                alertDialog2.show()
                                Toast.makeText(
                                    this,
                                    "Update is available! New Version $app_version_name",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        progressBar.setVisibility(
                            View.GONE
                        )
                        Toast.makeText(
                            applicationContext,
                            "Something Went Wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (exception: JSONException) {
                    exception.printStackTrace()
                    progressBar.setVisibility(
                        View.GONE
                    )
                    Toast.makeText(applicationContext, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }, { error ->
                progressBar.setVisibility(View.GONE)
                ll_no_internet.setVisibility(View.VISIBLE)
            }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["key"] = "key_pearl_06$$12$$2021"
                    params["code"] = "code_pearl_06$$12$$2021"
                    params["erp_name"] = "damasfollows"
                    params["app_name"] = "damasfollows"
                    Log.i("params ", "params : $params")
                    return params
                }
            }

        val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        requestQueue.add(stringRequest)


    }

    private fun callWebView(app_link: String, progressBar: ProgressBar?) {
        webView.webViewClient = WebViewClient()
        Log.d("url", app_link)
        webView.loadUrl(app_link)
        progressBar!!.visibility = View.GONE
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        // Handle Popups

        webView.setWebViewClient(object : MyWebViewClient(this) {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                if (url!!.startsWith("mailto:")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } else if (url.startsWith("https://api.whatsapp.com/")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    //                    view.loadUrl(url);
                } else if (url.startsWith("tel:")) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url)
                    Checkconnection(url)
                }
                return true
            }
        })
        webView.setWebChromeClient(object : WebChromeClient() {
            // openFileChooser for Android 3.0+
            // openFileChooser for Android < 3.0

            fun openFileChooser(uploadMsg: ValueCallback<Uri>?, acceptType: String? = "") {
                mUploadMessage = uploadMsg
                openImageChooser()
            }

            // For Lollipop 5.0+ Devices
            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mUploadMessages = filePathCallback
                openImageChooser()
                return true
            }

            //openFileChooser for other Android versions
            fun openFileChooser(
                uploadMsg: ValueCallback<Uri>?,
                acceptType: String?,
                capture: String?
            ) {
                openFileChooser(uploadMsg, acceptType)
            }

            private fun openImageChooser() {
                try {
                    val imageStorageDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "FolderName"
                    )
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs()
                    }
                    val file = File(
                        imageStorageDir.toString() + File.separator + "IMG_" + System.currentTimeMillis()
                            .toString() + ".jpg"
                    )
                    mCapturedImageURI = Uri.fromFile(file)
                    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type = "image/*"
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    val chooserIntent = Intent.createChooser(i, "Image Chooser")
                    chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        arrayOf<Parcelable>(captureIntent)
                    )
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        val conMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null) {
            webView.visibility = View.GONE
            ll_no_internet.setVisibility(
                View.VISIBLE
            )
        } else {
            Log.d("url", app_link)
            webView.loadUrl(app_link)
            progressBar!!.visibility = View.GONE
            webView.requestFocus()
            webView.visibility = View.VISIBLE
            ll_no_internet.setVisibility(
                View.GONE
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadMessages) {
                return
            }
            if (null != mUploadMessage) {
                handleUploadMessage(requestCode, resultCode, data)
            } else if (mUploadMessages != null) {
                handleUploadMessages(requestCode, resultCode, data)
            }
        }
    }

    private fun handleUploadMessage(requestCode: Int, resultCode: Int, data: Intent?) {
        var result: Uri? = null
        try {
            result = if (resultCode != RESULT_OK) {
                null
            } else {
                // retrieve from the private variable if the intent is null
                if (data == null) mCapturedImageURI else data.data
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mUploadMessage!!.onReceiveValue(result)
        mUploadMessage = null

        // code for all versions except of Lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            result = null
            try {
                result = if (resultCode != RESULT_OK) {
                    null
                } else {
                    // retrieve from the private variable if the intent is null
                    if (data == null) mCapturedImageURI else data.data
                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(applicationContext, "activity :$e", Toast.LENGTH_LONG).show()
            }
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        }
    } // end of code for all versions except of Lollipop


    private fun handleUploadMessages(requestCode: Int, resultCode: Int, data: Intent?) {
        var results: Array<Uri?>? = null
        try {
            if (resultCode != RESULT_OK) {
                results = null
            } else {
                if (data != null) {
                    val dataString = data.dataString
                    val clipData = data.clipData
                    if (clipData != null) {
                        results = arrayOfNulls(clipData.itemCount)
                        for (i in 0 until clipData.itemCount) {
                            val item = clipData.getItemAt(i)
                            results[i] = item.uri!!
                        }
                    }
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                } else {
                    results = arrayOf(mCapturedImageURI)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mUploadMessages!!.onReceiveValue(results as Array<Uri>)
        mUploadMessages = null
    }

    private fun Checkconnection(url: String) {
        val conMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null) {
            webView.visibility = View.GONE
            progressBar.setVisibility(View.GONE)
            ll_no_internet.setVisibility(
                View.VISIBLE
            )
        } else {
            webView.loadUrl(url)
            webView.requestFocus()
            webView.visibility = View.VISIBLE
            ll_no_internet.setVisibility(
                View.GONE
            )
            progressBar.setVisibility(View.GONE)
        }
    }

    private fun registerBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                netwokChangeReciever,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                netwokChangeReciever,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    protected fun unregisterBroadcast() {
        try {
            unregisterReceiver(netwokChangeReciever)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            val alertDialog2 = AlertDialog.Builder(
                this
            )
            alertDialog2.setTitle("Alert...")
            alertDialog2.setMessage("Are you sure you want to exit ?")
            alertDialog2.setPositiveButton(
                "Yes"
            ) { dialog: DialogInterface?, which: Int ->
                finishAffinity()
                System.exit(0)
            }
            alertDialog2.setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
            alertDialog2.show()
            //       finish();
        }
    }

    override fun onStart() {
        super.onStart()
        registerBroadcast()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcast()
    }

    fun bottomNavClick(){
        bottomNav.setOnItemSelectedListener {
        when(it.itemId){
    R.id.home->{
        webView.loadUrl(app_link)
        webView.requestFocus()
        true
    }
    R.id.list->{
        webView.loadUrl("https://damasfollows.com/services")
        webView.requestFocus()
        true
            }
            R.id.faq->{
                webView.loadUrl("https://damasfollows.com/faq")
                webView.requestFocus()
                true
            }
        }
            true
        }

    }
}