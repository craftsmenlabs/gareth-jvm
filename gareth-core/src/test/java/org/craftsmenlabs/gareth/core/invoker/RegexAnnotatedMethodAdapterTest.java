package org.craftsmenlabs.gareth.core.invoker;

import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.core.storage.DefaultStorage;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RegexAnnotatedMethodAdapterTest {

    @Test
    public void simpleLineWithoutStorage() throws NoSuchMethodException {
        final RegexAnnotatedMethodAdapter line =
                new RegexAnnotatedMethodAdapter(Simple.class.getMethod("line"), "simpleLineWithoutStorage");
        assertThat(line.getNonStorageParameters()).isEmpty();
        assertThat(line.getPattern()).isEqualTo("simpleLineWithoutStorage");
    }

    @Test
    public void withStorage() throws NoSuchMethodException {
        RegexAnnotatedMethodAdapter line =
                new RegexAnnotatedMethodAdapter(WithStorage.class.getMethod("line", DefaultStorage.class), "withStorage");
        assertThat(line.getNonStorageParameters()).isEmpty();
        assertThat(line.getPattern()).isEqualTo("withStorage");
    }

    @Test
    public void withStorageAndString() throws NoSuchMethodException {
        RegexAnnotatedMethodAdapter line =
                new RegexAnnotatedMethodAdapter(WithStorageAndString.class
                        .getMethod("line", DefaultStorage.class, String.class), "withStorageAndString");
        assertThat(line.getNonStorageParameters()).containsExactly(String.class);
        assertThat(line.getPattern()).isEqualTo("withStorageAndString");
    }

    @Test
    public void withStorageAndStringAndInt() throws NoSuchMethodException {
        RegexAnnotatedMethodAdapter line =
                new RegexAnnotatedMethodAdapter(WithStorageAndStringAndint.class
                        .getMethod("line", DefaultStorage.class, String.class, Integer.TYPE), "withStorageAndStringAndInt");
        assertThat(line.getNonStorageParameters()).containsExactly(String.class, Integer.TYPE);
        assertThat(line.getPattern()).isEqualTo("withStorageAndStringAndInt");
    }

    @Test
    public void withInvalidArgument() throws NoSuchMethodException {
        assertThatThrownBy(() -> new RegexAnnotatedMethodAdapter(WithInvalidArgument.class
                .getMethod("line", File.class), "withFile")).isInstanceOf(IllegalStateException.class);
    }


    @Test
    public void incorrectPatterns() throws NoSuchMethodException {
        RegexAnnotatedMethodAdapter descriptor =
                new RegexAnnotatedMethodAdapter(WithIntAndLongAndDouble.class
                        .getMethod("line", String.class, Integer.TYPE, Double.TYPE), "Bookings for (.*?) with (.*?) beds? should have increased by (.*?)%");
        assertThatThrownBy(() -> descriptor.getArgumentValuesFromInputString("Invalid string"))
                .hasMessageContaining("could not be matched against pattern");

    }

    @Test
    public void correctPatterns() throws NoSuchMethodException {
        RegexAnnotatedMethodAdapter descriptor =
                new RegexAnnotatedMethodAdapter(WithIntAndLongAndDouble.class
                        .getMethod("line", String.class, Integer.TYPE, Double.TYPE), "Bookings for (.*?) with (.*?) beds? should have increased by (.*?)%");
        List<Object> parametersFromPattern = descriptor
                .getArgumentValuesFromInputString("Bookings for double rooms with 1 beds should have increased by 5.2%");
        assertThat(parametersFromPattern).containsExactly("double rooms", 1, 5.2);
        WithIntAndLongAndDouble obj = new WithIntAndLongAndDouble();
        descriptor.invokeWith(obj, parametersFromPattern);
        assertThat(obj.result).isEqualTo("double rooms with 1 beds increased 5.2");
    }

    @Test
    public void testInvoke() throws NoSuchMethodException {
        RegexAnnotatedMethodAdapter descriptor =
                new RegexAnnotatedMethodAdapter(WithIntAndLongAndDouble.class
                        .getMethod("line", String.class, Integer.TYPE, Double.TYPE), "Bookings for (.*?) with (.*?) beds? should have increased by (.*?)%");
        assertThat(descriptor
                .getArgumentValuesFromInputString("Bookings for rooms with 1 bed should have increased by 5.2%"))
                .containsExactly("rooms", 1, 5.2);
        assertThat(descriptor
                .getArgumentValuesFromInputString("Bookings for suites with 2 beds should have increased by 12%"))
                .containsExactly("suites", 2, 12d);
    }

    static class Simple {
        @Baseline(glueLine = "simpleLineWithoutStorage")
        public void line() {

        }
    }

    static class WithStorage {
        @Baseline(glueLine = "withStorage")
        public void line(DefaultStorage storage) {

        }
    }

    static class WithStorageAndString {
        @Baseline(glueLine = "withStorageAndString")
        public void line(DefaultStorage storage, String string) {

        }
    }

    static class WithStorageAndStringAndint {
        @Baseline(glueLine = "withStorageAndStringAndInt")
        public void line(DefaultStorage storage, String string, int intVal) {
        }
    }

    static class WithInvalidArgument {
        @Baseline(glueLine = "withFile")
        public void line(File file) {
        }
    }

    class WithIntAndLongAndDouble {
        private String result;

        @Baseline(glueLine = "Bookings for (.*?) with (.*?) beds? should have increased by (.*?)%")
        public void line(String roomType, int beds, double doubleVal) {
            result = roomType + " with " + beds + " beds increased " + doubleVal;
        }
    }
}