package rocks.aston.hongguotv;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class PlayerRendererPolicyTest {
    @Test
    public void standardPlaybackUsesMedia3AsPrimary() {
        assertTrue(PlayerRendererPolicy.primaryPlayback());
    }
}
