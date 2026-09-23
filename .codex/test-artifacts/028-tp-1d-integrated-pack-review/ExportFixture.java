import com.oxygen.weather.data.*;
import com.oxygen.weather.derived.*;
import com.oxygen.weather.presentation.*;
import com.google.gson.*;
import java.time.*;
import java.util.*;
public class ExportFixture {
 public static void main(String[] args) {
  Locale.setDefault(Locale.US);
  var bundle = DemoWeatherRepository.INSTANCE.load(LocalDateTime.of(2026,9,23,9,0));
  var derived = HistoricalSynthesis.INSTANCE.derive(bundle);
  var home = HomePresentationMapper.INSTANCE.map(bundle,derived);
  var out = new LinkedHashMap<String,Object>();
  out.put("anchor", "2026-09-23T09:00:00 America/Chicago; Locale.US");
  out.put("presentation",home);
  var result = new WeatherRepositoryResult(bundle, WeatherDataOrigin.LIVE, WeatherFreshness.UNKNOWN, null, CacheWriteOutcome.NOT_ATTEMPTED);
  var state = (HomeLoadState.LiveData) HomePresentationMapper.INSTANCE.mapLoadState(new HomePresentationInput.Data(result,derived));
  out.put("status", state.getStatus().getVisibleText());
  System.out.println(new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(out));
 }
}
