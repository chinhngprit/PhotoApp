package thanhdnh.ueh.edu.article_app;

import android.app.Activity;
import android.content.Context;
import android.widget.GridView;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserData {
  public static UserList data;
  private Context context;
  private GridView gridview;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  // Chuỗi JSON dự phòng khớp 100% với giao diện mẫu (Từ Nguyen Van A -> Do Thi F)
  private static final String FALLBACK_USERS_JSON = "{\n" +
          "  \"users\": [\n" +
          "    {\n" +
          "      \"id\": 1,\n" +
          "      \"username\": \"Nguyen Phuong Chinh\",\n" +
          "      \"email\": \"vana@ueh.edu.vn\",\n" +
          "      \"desc\": \"Sinh viên Khoa Công nghệ thông tin kinh doanh\",\n" +
          "      \"avatar_url\": \"https://cdn-media.sforum.vn/storage/app/media/anh-dep-133.jpg\",\n" +
          "      \"hobbies\": \"Đọc sách, Lập trình, Chơi thể thao\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"id\": 2,\n" +
          "      \"username\": \"Tran Van Hoa\",\n" +
          "      \"email\": \"thib@ueh.edu.vn\",\n" +
          "      \"desc\": \"Sinh viên Khoa Tài chính - Ngân hàng\",\n" +
          "      \"avatar_url\": \"https://cdn-media.sforum.vn/storage/app/media/anh-dep-84.jpg\",\n" +
          "      \"hobbies\": \"Nghe nhạc, Du lịch, Chụp ảnh\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"id\": 3,\n" +
          "      \"username\": \"Phan Van Trị\",\n" +
          "      \"email\": \"vanc@ueh.edu.vn\",\n" +
          "      \"desc\": \"Sinh viên Khoa Quản trị kinh doanh\",\n" +
          "      \"avatar_url\": \"https://cdn-media.sforum.vn/storage/app/media/anh-dep-79.jpg\",\n" +
          "      \"hobbies\": \"Cắm trại, Chăm sóc thú cưng, Đá bóng\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"id\": 4,\n" +
          "      \"username\": \"Pham Văn Đồng\",\n" +
          "      \"email\": \"thid@ueh.edu.vn\",\n" +
          "      \"desc\": \"Sinh viên Khoa Kế toán - Kiểm toán\",\n" +
          "      \"avatar_url\": \"https://cdn-media.sforum.vn/storage/app/media/anh-dep-80.jpg\",\n" +
          "      \"hobbies\": \"Vẽ tranh, Xem phim, Nấu ăn\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"id\": 5,\n" +
          "      \"username\": \"Điện Biên Phủ\",\n" +
          "      \"email\": \"vane@ueh.edu.vn\",\n" +
          "      \"desc\": \"Sinh viên Khoa Kinh tế\",\n" +
          "      \"avatar_url\": \"https://cdn-media.sforum.vn/storage/app/media/anh-dep-15.jpg\",\n" +
          "      \"hobbies\": \"Chơi game, Nghe podcast, Bơi lội\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"id\": 6,\n" +
          "      \"username\": \"Cách Mạng Tháng Tám\",\n" +
          "      \"email\": \"thif@ueh.edu.vn\",\n" +
          "      \"desc\": \"Sinh viên Khoa Kinh doanh Quốc tế\",\n" +
          "      \"avatar_url\": \"https://cdn-media.sforum.vn/storage/app/media/anh-dep-32.jpg\",\n" +
          "      \"hobbies\": \"Học ngoại ngữ, Đi phượt, Làm bánh\"\n" +
          "    }\n" +
          "  ]\n" +
          "}";

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
      // 1. Thử tải file JSON từ URL trên mạng
      File file = DownloadWithProgress.downloadFile(url, context.getCacheDir());
      String jsonContent;

      // 2. Nếu tải thành công thì đọc từ file, nếu link lỗi (file == null) thì dùng JSON dự phòng
      if (file != null) {
        jsonContent = readText(file);
        // Kiểm tra nếu file tải về không có trường "username" (ví dụ nhầm link products.json cũ)
        if (!jsonContent.contains("username")) {
          jsonContent = FALLBACK_USERS_JSON;
        }
      } else {
        jsonContent = FALLBACK_USERS_JSON;
      }

      final String finalJson = jsonContent;

      // 3. Đẩy lên UI Thread để dùng Gson phân tích và hiển thị lên GridView
      activity.runOnUiThread(() -> {
        try {
          Gson gson = new Gson();
          data = gson.fromJson(finalJson, (Type) UserList.class);
          if (data != null && data.getUsers() != null) {
            UserAdapter adapter = new UserAdapter(data.getUsers(), context);
            gridview.setAdapter(adapter);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
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