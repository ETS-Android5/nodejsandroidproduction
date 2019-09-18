package android.gr.katastima;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CodePopClass extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_code);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int finalW = (int)(dm.widthPixels * .9);
        int finalH = finalW / 2;

        getWindow().setLayout(finalW, finalH);

        TextView codeView = (TextView) findViewById(R.id.code_view);
        codeView.setText(Functions.CURRENT_CODE);

        TextView emailView = (TextView) findViewById(R.id.email_view);
        emailView.setText(MainActivity.account.getEmail());

        Button b = (Button) findViewById(R.id.okButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
