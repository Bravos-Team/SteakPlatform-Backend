package com.bravos.steak.common.model;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.*;

@NoArgsConstructor
public class Trie {

    private final TrieNode root = new TrieNode();

    private Set<String> words = new HashSet<>();

    /**
     *
     * @param words Set từ khóa
     */
    public Trie(Collection<String> words) {
        this.words = new HashSet<>(words);
        words.forEach(word -> {
            TrieNode current = root;
            for (char ch : word.toCharArray()) {
                current = current.getChildren().computeIfAbsent(ch, c -> new TrieNode());
            }
        });
    }

    /**
     * Chèn dữ liệu vào từ điển
     *
     * @param word từ cần bỏ vào
     */
    public void insert(@NonNull String word) {
        if (words.add(word)) {
            TrieNode current = root;
            for (char ch : word.toCharArray()) {
                current = current.getChildren().computeIfAbsent(ch, c -> new TrieNode());
            }
        }
    }

    /**
     * Kiểm tra tiền tố có match ko
     *
     * @param prefix tiền tố cần kiểm
     * @return true: có
     */
    public boolean startsWith(@NonNull String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            current = current.getChildren().get(ch);
            if (current == null) return false;
        }
        return true;
    }

    /**
     * Search coi có không ?
     *
     * @param word từ cần search
     * @return true: có
     */
    public boolean contains(@NonNull String word) {
        return words.contains(word);
    }

    /**
     * Đếm số lượng từ
     * @return số lượng từ
     */
    public long getWordsCount() {
        return words.size();
    }



}
