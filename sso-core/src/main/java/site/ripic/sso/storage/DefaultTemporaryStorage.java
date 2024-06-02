package site.ripic.sso.storage;

import site.ripic.sso.exception.BadConfigException;

public class DefaultTemporaryStorage implements TemporaryStorage {
    @Override
    public void set(String key, String value, long timeout) {
        throw new BadConfigException("The temporaryStorage is not configured.");
    }

    @Override
    public String get(String key) {
        throw new BadConfigException("The temporaryStorage is not configured.");
    }

    @Override
    public void remove(String key) {
        throw new BadConfigException("The temporaryStorage is not configured.");
    }

    @Override
    public long getTtl(String key) {
        throw new BadConfigException("The temporaryStorage is not configured.");

    }

    @Override
    public void updateTimeout(String key, long timeout) {
        throw new BadConfigException("The temporaryStorage is not configured.");
    }

    @Override
    public void updateValue(String key, String value, long timeout) {
        throw new BadConfigException("The temporaryStorage is not configured.");
    }
}
