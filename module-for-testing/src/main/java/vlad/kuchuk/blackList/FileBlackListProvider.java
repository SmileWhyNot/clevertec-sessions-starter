package vlad.kuchuk.blackList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import vlad.kuchuk.service.BlackListProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileBlackListProvider implements BlackListProvider {
    private static final String FILE_PATH = "classpath:blacklist.txt";
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public Set<String> getBlackList() {
        try {
            Resource resource = resourceLoader.getResource(FILE_PATH);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            Set<String> blackList = reader.lines().collect(Collectors.toSet());

            reader.close();

            return blackList;
        } catch (IOException e) {
            log.error("Failed to read blacklist from file :" + e.getCause());
            return Collections.emptySet();
        }
    }
}