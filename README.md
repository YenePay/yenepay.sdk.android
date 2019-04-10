# Android SDK for YenePay
YenePay SDK for android will let you connect your app with YenePay to start accepting payments. 

## Installation
Add jitpack.io to the end of your repositories
```gradle
repositories {
    ...
    maven { url "https://jitpack.io" }
}
```
Add the SDK package to your project's build.gradle file. Make sure to use the latest version. You can find the latest version [here](https://https://github.com/YenePay/yenepay.sdk.android/releases).
```gradle
dependencies {
    ...
    implementation 'com.github.YenePay:yenepay.sdk.android:0.0.7'
}
```
Add yenepayReturnScheme to manifestPlaceholders in build.gradle file ofyour project. This uri scheme will be used to initiate a pending activity when payment response arrived. This  can be anything that is unique to your activity.

```gradle
android {
    ...

    defaultConfig {
        ...
        manifestPlaceholders = [
                yenepayReturnScheme: "com.yourappname.yenepay"
        ]

```
Change com.yourappname.yenepay with your own scheme. 


That's it your app can now start to accept payment using yenepay.

## Getting started

To start accepting payment the easies way is extending your activity from [YenePayPaymentActivity](https://https://github.com/YenePay/yenepay.sdk.android/blob/master/yeneSDK/src/main/java/com/yenepaySDK/YenePayPaymentActivity.java). See the example below

```Java
public class MainActivity extends YenePayPaymentActivity {
    ...
    private void checkout(){

        PaymentOrderManager paymentMgr = new PaymentOrderManager(
                "YOUR_YENEPAY_MERCHANT_CODE",
                "YOUR_GENERATED_ORDER_ID");
        paymentMgr.setPaymentProcess(PaymentOrderManager.PROCESS_CART);
        paymentMgr.setReturnUrl("com.yourappname.yenepay:/payment2redirect");
        paymentMgr.setUseSandboxEnabled(true);
        paymentMgr.addItem(new OrderedItem("ITEM_ID", "Item Name", 2, 12.70));
        paymentMgr.startCheckout(this);
    }
    @Override
    public void onPaymentResponseArrived(PaymentResponse response) {
        //Handle Payment response
        if(response.isPaymentCompleted()){
            //Complete delivery
        }
    }

    @Override
    public void onPaymentResponseError(String error) {
        //Handle payment request error.
        showMessage(error);
    }

```
This all it takes to accept payment from your android app. 

## Issues
If you encounter any issues please report them [here](https://github.com/YenePay/yenepay.sdk.android/issues) and we will try to provide a fix as soon as we can.

## More information

To find more information about YenePay integration please visit [YenePay Developers](https://yenepay.com/developers) or visit [YenePay Community Site](https://community.yenepay.com)