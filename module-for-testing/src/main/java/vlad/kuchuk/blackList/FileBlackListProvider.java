package vlad.kuchuk.blackList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
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
    private final String filepath;
    private final ResourceLoader resourceLoader;

    @Autowired
    public FileBlackListProvider(@Value("${blacklist.path}") String filepath) {
        this.filepath = filepath;
        this.resourceLoader = new DefaultResourceLoader();
    }


    @Override
    public Set<String> getBlackList() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resourceLoader.getResource(filepath).getInputStream()))
        ) {
            return reader.lines().collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Failed to read blacklist from file :", e);
            return Collections.emptySet();
        }
    }
}