package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Tag;

public interface ITag {
    Tag getByName(String name);

    Tag create(Tag tag);
}
