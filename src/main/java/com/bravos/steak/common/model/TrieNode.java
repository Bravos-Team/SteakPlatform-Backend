package com.bravos.steak.common.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TrieNode {

    private final Map<Character,TrieNode> children = new HashMap<>();

}
