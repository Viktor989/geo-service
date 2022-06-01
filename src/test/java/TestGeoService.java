import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;

public class TestGeoService {
    @ParameterizedTest
    @MethodSource("testArguments")
    public void MessageSenderImpl(Location location, String ip, String mess) {
        GeoService geoService = Mockito.mock(GeoServiceImpl.class);
        Mockito.when(geoService.byIp(ip))
                .thenReturn(location);

        LocalizationService localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationService.locale(location.getCountry()))
                .thenReturn(mess);

        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);

        String resultTest = messageSender.send(headers);
        Assertions.assertEquals(resultTest, mess);
    }

    @ParameterizedTest
    @MethodSource("testArguments")
    void testGeoServiceImpl(Location location, String ip, String mess) {
        GeoService geoService = new GeoServiceImpl();
        Location result = geoService.byIp(ip);
        Assertions.assertEquals(result, location);
    }

    @ParameterizedTest
    @CsvSource({"RUSSIA, Добро пожаловать", "USA, Welcome"})
    void testLocalizationServiceImpl(Country country, String string) {
        LocalizationService localizationService = new LocalizationServiceImpl();
        String result = localizationService.locale(country);
        Assertions.assertEquals(result, string);
    }

    private static Stream<Arguments> testArguments() {
        Location[] location = new Location[2];
        location[0] = new Location("Moscow", Country.RUSSIA, "Lenina", 15);
        location[1] = new Location("New York", Country.USA, " 10th Avenue", 32);
        return Stream.of(
                Arguments.of(location[0], "172.0.32.11", "Добро пожаловать"),
                Arguments.of(location[1], "96.44.183.149", "Welcome"));
    }
}
