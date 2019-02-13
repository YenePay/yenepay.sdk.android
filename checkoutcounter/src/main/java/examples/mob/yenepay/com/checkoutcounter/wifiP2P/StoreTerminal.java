package examples.mob.yenepay.com.checkoutcounter.wifiP2P;
import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;

import android.arch.lifecycle.MutableLiveData;

public class StoreTerminal{
    public final String terminalCode;
    public String ssid;
    public String terminalName;
    public String networkPass;
    public String host;
    public MutableLiveData<TerminalStatus> status;

    public StoreTerminal() {
        terminalName =StoreManager.getStoreTerminalName();
        terminalCode = StoreManager.getStoreCode();
        status = new MutableLiveData<>();
        status.setValue(TerminalStatus.NotStarted);
    }

    public void setStatus(TerminalStatus status) {
        this.status.setValue(status);
    }


    public String getStatusText(){
        return status.getValue().name();
    }
}