package uk.co.imallan.jellyrefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import uk.co.imallan.jellyrefresh.JellyRefreshLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Jelly");
        final JellyRefreshLayout jellyLayout = (JellyRefreshLayout) findViewById(R.id.jelly_refresh);
        jellyLayout.setRefreshListener(new JellyRefreshLayout.JellyRefreshListener() {
            @Override
            public void onRefresh(final JellyRefreshLayout jellyRefreshLayout) {
                jellyRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jellyRefreshLayout.finishRefreshing();
                    }
                }, 3000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
