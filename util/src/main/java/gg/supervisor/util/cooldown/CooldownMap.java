package gg.supervisor.util.cooldown;

import java.util.HashMap;

public class CooldownMap<T> extends HashMap<T, Long> {

    /**
     * Returns the amount of time passed in seconds. Returns -1 if the player isn't in the list
     *
     * @return the timed passed in seconds
     */
    public double getElapsed(T t) {

        if (!containsKey(t)) return -1;

        return System.currentTimeMillis() / 1000d - (get(t) / 1000d);

    }

    /**
     * This function is used to test if the specific identifier is under cooldown.
     *
     * @param t the unique identifier, usually a UUID
     * @param seconds the amount to check in seconds.
     * @param silent rather it set the cooldown if the return is false.
     *
     * @return false if the player is not on cooldown true if the player is on cooldown
     */
    public boolean testCooldown(T t, double seconds, boolean silent) {

        if (!containsKey(t) || getElapsed(t) >= seconds) {

            if (!silent) triggerCooldown(t);

            return false;
        }

        return true;
    }

    /**
     *
     * This function is used to test if the specific identifier is under cooldown. and sets the cooldown if not.
     *
     * @param t the unique identifier, usually a UUID
     * @param seconds the amount to check in seconds.
     * @return false if the player is not on cooldown true if the player is on cooldown
     */
    public boolean testCooldown(T t, double seconds) {
        return testCooldown(t, seconds, false);
    }

    public void triggerCooldown(T t) {
        put(t, System.currentTimeMillis());
    }

}
