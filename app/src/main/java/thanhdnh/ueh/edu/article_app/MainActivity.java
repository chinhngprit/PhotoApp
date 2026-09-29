package thanhdnh.ueh.edu.article_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  public GridView gridview;

  private AdapterView.OnItemClickListener onitemclick = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Intent intent = new Intent(getBaseContext(), ViewUserActivity.class);
      intent.putExtra("id", gridview.getAdapter().getItemId(position));
      startActivity(intent);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    gridview = findViewById(R.id.gridview);

    // Đã thay bằng link GitHub Gist của bạn
    String gistUrl = "https://gist.githubusercontent.com/chinhngprit/ad35a94d840f8a52a543931afcc24eb6/raw/3d7f7847da9b496eeefc7ab47bac71593125a104/gistfile1.txt";
    new UserData(getBaseContext(), gridview).loadData(gistUrl, this);

    gridview.setOnItemClickListener(onitemclick);
  }
}