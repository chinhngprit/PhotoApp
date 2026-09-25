package thanhdnh.ueh.edu.article_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ViewUserActivity extends AppCompatActivity {
  ImageView iv_detail;
  TextView tv_detail_title, tv_detail_email, tv_detail_description, tv_detail_hobby;
  ProgressBar progress_bar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_user);

    // Hiển thị thanh Action Bar màu tím với tiêu đề "User Detail" và nút Back
    if (getSupportActionBar() != null) {
      getSupportActionBar().show();
      getSupportActionBar().setTitle("User Detail");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    iv_detail = findViewById(R.id.iv_detail);
    tv_detail_title = findViewById(R.id.tv_detail_title);
    tv_detail_email = findViewById(R.id.tv_detail_email);
    tv_detail_description = findViewById(R.id.tv_detail_description);
    tv_detail_hobby = findViewById(R.id.tv_detail_hobby);
    progress_bar = findViewById(R.id.progress_bar);

    int id = (int) getIntent().getLongExtra("id", 0);
    UserProfile user = UserData.getUserFromId(id);

    if (user != null) {
      tv_detail_title.setText(user.getUsername());
      tv_detail_email.setText("Email: " + user.getEmail());
      tv_detail_description.setText("Description: " + user.getDesc());
      tv_detail_hobby.setText("Hobby: " + user.getHobbies());

      // Tải ảnh với thanh tiến trình (Chức năng 3)
      Handler mainHandler = new Handler(Looper.getMainLooper());
      DownloadWithProgress.downloadWithProgress(
              user.getAvatar_url(),
              mainHandler,
              this,
              getCacheDir(),
              progress_bar,
              iv_detail
      );
    }
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}