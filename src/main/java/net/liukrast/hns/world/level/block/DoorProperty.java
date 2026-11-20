package net.liukrast.hns.world.level.block;

import net.liukrast.hns.world.level.block.entity.DoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record DoorProperty<T extends DoorBlockEntity, V>(
        String key, String category, Class<T> type, V defaultValue, Function<T, V> get, BiConsumer<T, V> set, @Nullable Range range) {

    public static final String GENERAL = "general";

    public static String id(Enum<?> e) {
        return e.name().toLowerCase(Locale.ROOT);
    }
    public static Object serialize(Object v) {
        return v instanceof Enum<?> e ? id(e) : v;
    }
    public record Range(float min, float max, float step) {}

    private @Nullable Double number(Object raw) {
        if (!(raw instanceof Number n) || !Double.isFinite(n.doubleValue())) return null;
        double v = n.doubleValue();
        return range == null ? v : Math.clamp(v, range.min(), range.max());
    }

    public V fetch(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return type.isInstance(be) ? get.apply(type.cast(be)) : defaultValue;
    }

    public boolean apply(Level level, BlockPos pos, Object raw) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!type.isInstance(be)) return false;
        V value = coerce(raw);
        if (value == null) return false;
        set.accept(type.cast(be), value);
        be.setChanged();
        return true;
    }

    @SuppressWarnings("unchecked")
    private V coerce(Object raw) {
        Object d = defaultValue;
        Object out = null;
        if (d instanceof Boolean) out = raw instanceof Boolean b ? b : null;
        else if (d instanceof Integer) { Double n = number(raw); out = n == null ? null : (Integer) n.intValue(); }
        else if (d instanceof Long)    { Double n = number(raw); out = n == null ? null : (Long) n.longValue(); }
        else if (d instanceof Float)   { Double n = number(raw); out = n == null ? null : (Float) n.floatValue(); }
        else if (d instanceof Double)  { out = number(raw); }
        else if (d instanceof String) out = raw instanceof String s ? s : null;
        else if (d instanceof Enum<?> e && raw instanceof String s) {
            for (Enum<?> c : e.getDeclaringClass().getEnumConstants()) {
                if (id(c).equals(s)) { out = c; break; }
            }
        }
        return (V) out;
    }

    public List<String> options() {
        if (!(defaultValue instanceof Enum<?> e)) return List.of();
        return Arrays.stream(e.getDeclaringClass().getEnumConstants()).map(DoorProperty::id).toList();
    }

    public void applyDefault(BlockEntity be) {
        if (type.isInstance(be)) {
            set.accept(type.cast(be), defaultValue);
        }
    }

    public boolean isDefault(Level level, BlockPos pos) {
        return Objects.equals(fetch(level, pos), defaultValue);
    }

    public boolean isEnum() {
        return defaultValue instanceof Enum<?>;
    }

    public boolean isConfigurable() {
        return !isEnum() || options().size() > 1;
    }
}