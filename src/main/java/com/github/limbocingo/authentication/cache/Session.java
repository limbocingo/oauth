package com.github.limbocingo.authentication.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Temporal cache for the sessions of the server.
 */
public class Session {
    public static HashMap<UUID, Integer> TFAPlayers = new HashMap<>();
    public static List<UUID> QueuePlayers = new ArrayList<>();
}
