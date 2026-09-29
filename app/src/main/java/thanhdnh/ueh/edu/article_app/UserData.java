package thanhdnh.ueh.edu.article_app;

import android.app.Activity;
import android.content.Context;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserData {
  public static UserList data;
  private Context context;
  private GridView gridview;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  public UserData(Context context, GridView gridview) {
    this.context = context;
    this.gridview = gridview;
  }

  public static UserProfile getUserFromId(int id) {
    if (data == null || data.getUsers() == null) return null;
    for (int i = 0; i < data.getUsers().size(); i++)
      if (data.getUsers().get(i).getId() == id)
        return data.getUsers().get(i);
    return null;
  }

  public void loadData(String url, Activity activity) {
    executor.execute(() -> {
      // Tải trực tiếp file từ link GitHub Gist về bộ nhớ Cache
      File file = DownloadWithProgress.downloadFile(url, context.getCacheDir());

      if (file != null) {
        String jsonText = readText(file).trim();

        activity.runOnUiThread(() -> {
          try {
            Gson gson = new Gson();

            // Trường hợp 1: Nếu Gist bắt đầu bằng dấu '[' (Mảng JSON trực tiếp: [ {...}, {...} ])
            if (jsonText.startsWith("[")) {
              Type listType = new TypeToken<ArrayList<UserProfile>>(){}.getType();
              ArrayList<UserProfile> userArray = gson.fromJson(jsonText, listType);
              data = new UserList(userArray);
            }
            // Trường hợp 2: Nếu Gist bắt đầu bằng dấu '{' (Object bọc mảng: { "users": [ ... ] })
            else {
              data = gson.fromJson(jsonText, (Type) UserList.class);
            }

            // Đổ dữ liệu lên GridView
            if (data != null && data.getUsers() != null) {
              UserAdapter adapter = new UserAdapter(data.getUsers(), context);
              gridview.setAdapter(adapter);
            } else {
              Toast.makeText(context, "Cấu trúc JSON trên Gist chưa khớp tên trường!", Toast.LENGTH_LONG).show();
            }
          } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi phân tích JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
          }
        });
      } else {
        activity.runOnUiThread(() ->
                Toast.makeText(context, "Không thể tải file từ GitHub Gist!", Toast.LENGTH_LONG).show()
        );
      }
    });
  }

  public String readText(File file) {
    StringBuffer buffer = new StringBuffer();
    try (InputStream stream = new FileInputStream(file);
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line).append("\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return buffer.toString();
  }
}