package com.brew.auth.service;

import com.brew.auth.entity.AppSetting;
import com.brew.auth.repository.AppSettingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AppSettingService {

    private static final Map<String, String> DEFAULTS = Map.of(
        "login_title", "Brew Auth"
    );

    private final AppSettingRepository repository;

    public AppSettingService(AppSettingRepository repository) {
        this.repository = repository;
    }

    public String get(String key) {
        return repository.findById(key)
            .map(AppSetting::getValue)
            .orElse(DEFAULTS.getOrDefault(key, ""));
    }

    public Map<String, String> getAll() {
        Map<String, String> result = new HashMap<>(DEFAULTS);
        repository.findAll().forEach(s -> result.put(s.getKey(), s.getValue()));
        return result;
    }

    public void set(String key, String value) {
        AppSetting setting = repository.findById(key)
            .orElse(new AppSetting(key, value));
        setting.setValue(value);
        setting.setUpdatedAt(LocalDateTime.now());
        repository.save(setting);
    }
}
