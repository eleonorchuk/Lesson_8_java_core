import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, Exception {
        //
        OkHttpClient client = new OkHttpClient();

        // Экземпляр класса Request создается через Builder (см. паттерн проектирования "Строитель")
        Request request = new Request.Builder()
                .url("http://dataservice.accuweather.com/forecasts/v1/daily/5day/295212?apikey=pjllXlSxW8ER3AM9vGq31S3DfGCblEkN")
                .build();
        // Получение объекта ответа от сервера
        Response response = client.newCall(request).execute();

        System.out.println(response.code());
        // Тело сообщения возвращается методом body объекта Response
        String body = response.body().string();
        System.out.println(body);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        JsonNode jsonNode = objectMapper.readTree(body);
        JsonNode dailyForecastJn = jsonNode.get("DailyForecasts");
        String dailyForecast = dailyForecastJn.toString();
        List<WeatherResponse> weatherResponseList = objectMapper.readValue(dailyForecast.toLowerCase(), new TypeReference<List<WeatherResponse>>(){});
        System.out.println(weatherResponseList);

        DBRepository db = new DBRepository("jdbc:sqlite:mydatabase.db");
        for (WeatherResponse wr: weatherResponseList) {
            double temp = wr.getTemperature().get("minimum").get("value").asDouble();
            db.addDate("Saint-Petersburg",wr.getDate(),wr.getDay().get("iconphrase").asText(), temp);
        }
        List<DBRepository.DateDb> dbdata= db.readDates();
        System.out.println("Database data:");
        for (DBRepository.DateDb wr: dbdata) {
            System.out.println(wr);
        }
    }
}
