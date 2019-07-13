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
Add the SDK package to your project's build.gradle file. Make sure to use the latest version. You can find the latest version [here](https://github.com/YenePay/yenepay.sdk.android/releases).
```gradle
dependencies {
    ...
    implementation 'com.github.YenePay:yenepay.sdk.android:0.0.10'
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

To start accepting payment the easies way is extending your activity from [YenePayPaymentActivity](https://github.com/YenePay/yenepay.sdk.android/blob/master/yeneSDK/src/main/java/com/yenepaySDK/YenePayPaymentActivity.java). See the example below

```Java
public class MainActivity extends YenePayPaymentActivity {
    ...
    private void checkout(){

        PaymentOrderManager paymentMgr = new PaymentOrderManager(
                "YOUR_YENEPAY_MERCHANT_CODE",
                "YOUR_GENERATED_ORDER_ID");
        paymentMgr.setPaymentProcess(PaymentOrderManager.PROCESS_CART);
        paymentMgr.setReturnUrl("com.yourappname.yenepay:/payment2redirect");
        //If you want to move to production just omit this line
        paymentMgr.setUseSandboxEnabled(true);     
        //This will disable shopping cart mode to enable set it true.
        paymentMgr.setShoppingCartMode(false);
        try {
            paymentMgr.addItem(new OrderedItem("ITEM_ID", "Item Name", 2, 12.70));
            paymentMgr.startCheckout(this);
         } catch (InvalidPaymentException e) {
            Log.e(TAG, "checkoutWithBrowser: ", e);
            showErrorDialog(context, e.getMessage());
        }
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

If you want your app to handle payment responses even after it has been closed. You need to configure globally pending intents for the response.

One of a good places to put that logic can be in your Application class like this.

```Java
public class ShopApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PendingIntent completionIntent = PendingIntent.getActivity(getApplicationContext(),
                PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE,
                new Intent(getApplicationContext(), PaymentResponseActivity.class), 0);
        PendingIntent cancelationIntent = PendingIntent.getActivity(getApplicationContext(),
                PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE,
                new Intent(getApplicationContext(), PaymentResponseActivity.class), 0);
        YenePayConfiguration.setDefaultInstance(new YenePayConfiguration.Builder(getApplicationContext())
        .setGlobalCompletionIntent(completionIntent)
        .setGlobalCancelIntent(cancelationIntent)
        .build());
    }
}

```

## Validation
Since version [0.0.10](https://github.com/YenePay/yenepay.sdk.android/releases/tag/0.0.10) payment will be validated by [PaymentOrderManager](https://github.com/YenePay/yenepay.sdk.android/blob/master/yeneSDK/src/main/java/com/yenepaySDK/PaymentOrderManager.java) when adding item and starting checkout methodsand checking out. Both will throw an [InvalidPaymentException](https://github.com/YenePay/yenepay.sdk.android/blob/master/yeneSDK/src/main/java/com/yenepaySDK/errors/InvalidPaymentException.java)

```Java
try {
        OrderedItem orderedItem = new OrderedItem("ITEM_ID", "Item Name", 2, 12.70);
        paymentMgr.addItem(orderedItem);
        paymentMgr.startCheckout(this);
    } catch (InvalidPaymentException e) {
        Log.e(TAG, "checkoutWithBrowser: ", e);
        showErrorDialog(context, e.getMessage());
    }

    //If you want to validate manually

    //Payment validation
    PaymentOrderManager.PaymentValidationResult result = paymentMgr.validate();

    //Or for item validation 

    PaymentOrderManager.PaymentValidationResult result = paymentMgr.validateOrderedItem(orderedItem);

    //To check result
    if(!result.isValid){
        throw new InvalidPaymentException(result.toString());
    }
````
## Shopping cart mode

Shopping Cart mode is the default implementation of Ordered item list handling of the [PaymentOrderManager](https://github.com/YenePay/yenepay.sdk.android/blob/master/yeneSDK/src/main/java/com/yenepaySDK/PaymentOrderManager.java) when adding items. Basically it acts as a shopping cart and add an item only if any previous item with the same item id does not exist already. If it founds a match from previously added items it will only update the previous items quantity.

```Java

        String id = UUID.randomUUID().toString();
        OrderedItem first = new OrderedItem(id, "Test Item", 1, 15);
        OrderedItem second = new OrderedItem(id, "Test Item", 1, 15);
        first.setItemId(id);
        second.setItemId(id);
        int totalQty = first.getQuantity() + second.getQuantity();
        double totalPrice = first.getItemTotalPrice() + second.getItemTotalPrice();

        try {
            manager.addItem(first);
            manager.addItem(second);
        } catch (InvalidPaymentException e) {
            fail("Adding item failed");
        }

        assertEquals(1, manager.getItems().size());
        assertEquals(totalQty, manager.getItems().get(0).getQuantity());
        assertEquals(totalPrice, manager.getItems().get(0).getItemTotalPrice(), 0);

```

Since version [0.0.10](https://github.com/YenePay/yenepay.sdk.android/releases/tag/0.0.10) you can disable this feature which will allow you to add items with the same id more than once.
```Java

        manager.setShoppingCartMode(false);

```

This is all it takes to accept payment using [YenePay](https://www.yenepay.com) from your android app. 

## The sample shop simulator app
To run the sample app download or clone the repository then build and run the project <br/>
![YenePay Shop Simulator Sample App](https://github.com/YenePay/yenepay.sdk.android/blob/master/screenshots/device-2019-07-04-031141.png) ![YenePay Shop Simulator sample App](https://github.com/YenePay/yenepay.sdk.android/blob/master/screenshots/device-2019-07-04-031105.png)

Before you start checking out orders you need to provide YenePay MerchantCode on for this app. Go to [YenePay Sandbox](https://sandbox.yenepay.com) to create a testing merchant account. You can find more info [here](https://commuity.yenepay.com).

Set your merchant code on the setting page of the app. If you are working the sandbox you have to enable the Use Sandbox setings on also.

![YenePay Shop Simulator Sample App](https://github.com/YenePay/yenepay.sdk.android/blob/master/screenshots/device-2019-07-04-031258.png)


**Note**
If your app targets below Android API level 21 (LOLLIPOP) or if YenePay app is not installed on the user device, then configuring `YenePayConfiguration` with global PendingIntents like the above example is required to function return results properly.

Please see the sample app in this repository for more on how to achieve that.

## Issues
If you encounter any issues please report them [here](https://github.com/YenePay/yenepay.sdk.android/issues) and we will try to provide a fix as soon as we can.

## More information

To find more information about YenePay integration please visit [YenePay Developers](https://yenepay.com/developers) or visit [YenePay Community Site](https://community.yenepay.com)
