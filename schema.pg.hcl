schema "public" {
  comment = "Main schema"
}

table "game" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "publisher_id" {
    null = false
    type = bigint
  }
  column "name" {
    null = false
    type = character_varying(255)
  }
  column "price" {
    null = false
    type = numeric(13, 2)
  }
  column "status" {
    null = false
    type = integer
  }
  column "release_date" {
    null = false
    type = bigint
  }
  column "created_at" {
    null = false
    type = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null = false
    type = bigint
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "game_publisher_id_fkey" {
    columns     = [column.publisher_id]
    ref_columns = [table.publisher.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_game_name" {
    columns = [column.name]
  }
  index "idx_game_publisher" {
    columns = [column.publisher_id]
  }
  index "idx_game_release_date" {
    columns = [column.release_date]
  }
}
table "game_genre" {
  schema = schema.public
  column "game_id" {
    null = false
    type = bigint
  }
  column "genre_id" {
    null = false
    type = bigint
  }
  primary_key {
    columns = [column.game_id, column.genre_id]
  }
  foreign_key "game_genre_game_id_fkey" {
    columns     = [column.game_id]
    ref_columns = [table.game.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "game_genre_genre_id_fkey" {
    columns     = [column.genre_id]
    ref_columns = [table.genre.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_genre_game" {
    columns = [column.game_id]
  }
  index "idx_genre_genre" {
    columns = [column.genre_id]
  }
}
table "game_tag" {
  schema = schema.public
  column "game_id" {
    null = false
    type = bigint
  }
  column "tag_id" {
    null = false
    type = bigint
  }
  primary_key {
    columns = [column.game_id, column.tag_id]
  }
  foreign_key "game_tag_game_id_fkey" {
    columns     = [column.game_id]
    ref_columns = [table.game.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "game_tag_tag_id_fkey" {
    columns     = [column.tag_id]
    ref_columns = [table.tag.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_game_tag_game_id" {
    columns = [column.game_id]
  }
  index "idx_game_tag_tag_id" {
    columns = [column.tag_id]
  }
}
table "game_version" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "game_id" {
    null = false
    type = bigint
  }
  column "name" {
    null = false
    type = character_varying(255)
  }
  column "change_log" {
    null = true
    type = text
  }
  column "exec_path" {
    null = false
    type = character_varying(255)
  }
  column "download_url" {
    null = false
    type = character_varying(255)
  }
  column "status" {
    null = false
    type = integer
  }
  column "release_date" {
    null = false
    type = bigint
  }
  column "created_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "game_version_game_id_fkey" {
    columns     = [column.game_id]
    ref_columns = [table.game.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_release_date" {
    columns = [column.game_id, column.release_date, column.status]
  }
  index "idx_version_game" {
    columns = [column.game_id]
  }
}
table "genre" {
  schema = schema.public
  column "id" {
    null = false
    type = integer
    identity {
      generated = ALWAYS
      start     = 1
      increment = 1
    }
  }
  column "name" {
    null = false
    type = character_varying(64)
  }
  column "description" {
    null = true
    type = text
  }
  column "slug" {
    null = false
    type = character_varying(128)
  }
  primary_key {
    columns = [column.id]
  }
  index "idx_genre_slug" {
    columns = [column.slug]
  }
  unique "genre_slug_key" {
    columns = [column.slug]
  }
}
table "publisher" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "name" {
    null = false
    type = character_varying(255)
  }
  column "email" {
    null = false
    type = character_varying(255)
  }
  column "phone" {
    null = false
    type = character_varying(15)
  }
  column "status" {
    null = false
    type = smallint
  }
  column "logo_url" {
    null = true
    type = character_varying(255)
  }
  column "created_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  unique "publisher_email_key" {
    columns = [column.email]
  }
  unique "publisher_name_key" {
    columns = [column.name]
  }
}
table "publisher_account" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "publisher_id" {
    null = false
    type = bigint
  }
  column "username" {
    null = false
    type = character_varying(32)
  }
  column "password" {
    null = false
    type = character_varying(255)
  }
  column "email" {
    null = false
    type = character_varying(255)
  }
  column "status" {
    null    = false
    type    = smallint
    default = 0
  }
  column "enable_mfa" {
    null    = false
    type    = boolean
    default = false
  }
  column "mfa_secret" {
    null = true
    type = character_varying(255)
  }
  column "created_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null    = true
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "publisher_account_publisher_id_fkey" {
    columns     = [column.publisher_id]
    ref_columns = [table.publisher.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_account_publisher" {
    columns = [column.publisher_id]
  }
  index "idx_publisher_email" {
    columns = [column.email]
  }
  index "idx_publisher_username" {
    columns = [column.username]
  }
  unique "publisher_account_email_key" {
    columns = [column.email]
  }
  unique "publisher_account_username_key" {
    columns = [column.username]
  }
}
table "publisher_account_role" {
  schema = schema.public
  column "publisher_account_id" {
    null = false
    type = bigint
  }
  column "publisher_role_id" {
    null = false
    type = bigint
  }
  primary_key {
    columns = [column.publisher_account_id, column.publisher_role_id]
  }
  foreign_key "publisher_account_role_publisher_account_id_fkey" {
    columns     = [column.publisher_account_id]
    ref_columns = [table.publisher_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "publisher_account_role_publisher_role_id_fkey" {
    columns     = [column.publisher_role_id]
    ref_columns = [table.publisher_role.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_publisher_account_role_account_id" {
    columns = [column.publisher_account_id]
  }
  index "idx_publisher_account_role_both" {
    columns = [column.publisher_account_id, column.publisher_role_id]
  }
}
table "publisher_permission" {
  schema = schema.public
  column "id" {
    null = false
    type = integer
    identity {
      generated = ALWAYS
      start     = 1
      increment = 1
    }
  }
  column "group_id" {
    null = false
    type = integer
  }
  column "name" {
    null = false
    type = character_varying(64)
  }
  column "description" {
    null = true
    type = character_varying(255)
  }
  column "authorities" {
    null = false
    type = jsonb
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "publisher_permission_group_id_fkey" {
    columns     = [column.group_id]
    ref_columns = [table.publisher_permission_group.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  unique "publisher_permission_name_key" {
    columns = [column.name]
  }
}
table "publisher_permission_group" {
  schema = schema.public
  column "id" {
    null = false
    type = integer
    identity {
      generated = ALWAYS
      start     = 1
      increment = 1
    }
  }
  column "name" {
    null = false
    type = character_varying(255)
  }
  column "description" {
    null = true
    type = character_varying(255)
  }
  primary_key {
    columns = [column.id]
  }
}
table "publisher_permission_role" {
  schema = schema.public
  column "publisher_role_id" {
    null = false
    type = bigint
  }
  column "publisher_permission_id" {
    null = false
    type = integer
  }
  primary_key {
    columns = [column.publisher_role_id, column.publisher_permission_id]
  }
  foreign_key "publisher_permission_role_publisher_permission_id_fkey" {
    columns     = [column.publisher_permission_id]
    ref_columns = [table.publisher_permission.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "publisher_permission_role_publisher_role_id_fkey" {
    columns     = [column.publisher_role_id]
    ref_columns = [table.publisher_role.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_permission_id" {
    columns = [column.publisher_permission_id]
  }
  index "idx_role_id" {
    columns = [column.publisher_role_id]
  }
}
table "publisher_refresh_token" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "account_id" {
    null = false
    type = bigint
  }
  column "device_id" {
    null = false
    type = character_varying(64)
  }
  column "issues_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "expires_at" {
    null = false
    type = bigint
  }
  column "revoked" {
    null    = false
    type    = boolean
    default = false
  }
  column "token" {
    null = false
    type = character_varying(64)
  }
  column "device_info" {
    null = false
    type = character_varying(255)
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "publisher_refresh_token_account_id_fkey" {
    columns     = [column.account_id]
    ref_columns = [table.publisher_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_publisher_account_id" {
    columns = [column.account_id]
  }
  index "idx_publisher_token_device_id" {
    columns = [column.token, column.device_id]
  }
  unique "publisher_refresh_token_token_key" {
    columns = [column.token]
  }
}
table "publisher_role" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "publisher_id" {
    null = true
    type = bigint
  }
  column "name" {
    null = false
    type = character_varying(255)
  }
  column "active" {
    null    = false
    type    = boolean
    default = true
  }
  column "description" {
    null = true
    type = character_varying(255)
  }
  column "updated_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "publisher_role_publisher_id_fkey" {
    columns     = [column.publisher_id]
    ref_columns = [table.publisher.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_publisher_role_publisher_id" {
    columns = [column.publisher_id]
  }
}
table "tag" {
  schema = schema.public
  column "id" {
    null = false
    type = integer
    identity {
      generated = ALWAYS
      start     = 1
      increment = 1
    }
  }
  column "name" {
    null = false
    type = character_varying(64)
  }
  column "description" {
    null = true
    type = text
  }
  column "slug" {
    null = false
    type = character_varying(128)
  }
  primary_key {
    columns = [column.id]
  }
  index "idx_tag_slug" {
    columns = [column.slug]
  }
  unique "tag_slug_key" {
    columns = [column.slug]
  }
}
table "user_account" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "username" {
    null = false
    type = character_varying(32)
  }
  column "password" {
    null = false
    type = character_varying(255)
  }
  column "email" {
    null = false
    type = character_varying(255)
  }
  column "status" {
    null    = false
    type    = smallint
    default = 0
  }
  column "enable_mfa" {
    null    = false
    type    = boolean
    default = false
  }
  column "mfa_secret" {
    null = true
    type = character_varying(255)
  }
  column "created_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null    = true
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  index "idx_email" {
    columns = [column.username]
  }
  index "idx_username" {
    columns = [column.username]
  }
  unique "user_account_email_key" {
    columns = [column.email]
  }
  unique "user_account_username_key" {
    columns = [column.username]
  }
}
table "user_oauth2_account" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "oauth2_id" {
    null = false
    type = character_varying(255)
  }
  column "oauth2_provider" {
    null = false
    type = character_varying(255)
  }
  column "status" {
    null = false
    type = smallint
  }
  column "user_account_id" {
    null = false
    type = bigint
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "user_oauth2_account_user_account_id_fkey" {
    columns     = [column.user_account_id]
    ref_columns = [table.user_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_oauth2_id_provider" {
    columns = [column.oauth2_id, column.oauth2_provider]
  }
  index "idx_user_account_id" {
    columns = [column.user_account_id]
  }
}
table "user_refresh_token" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "account_id" {
    null = false
    type = bigint
  }
  column "device_id" {
    null = false
    type = character_varying(64)
  }
  column "issues_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "expires_at" {
    null = false
    type = bigint
  }
  column "revoked" {
    null    = false
    type    = boolean
    default = false
  }
  column "token" {
    null = false
    type = character_varying(64)
  }
  column "device_info" {
    null = false
    type = character_varying(255)
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "user_refresh_token_account_id_fkey" {
    columns     = [column.account_id]
    ref_columns = [table.user_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_account_id" {
    columns = [column.account_id]
  }
  index "idx_token_device_id" {
    columns = [column.token, column.device_id]
  }
  unique "user_refresh_token_token_key" {
    columns = [column.token]
  }
}
table "admin_account" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "username" {
    null = false
    type = character_varying(32)
  }
  column "password" {
    null = false
    type = character_varying(255)
  }
  column "email" {
    null = false
    type = character_varying(255)
  }
  column "status" {
    null    = false
    type    = smallint
    default = 0
  }
  column "enable_mfa" {
    null    = false
    type    = boolean
    default = false
  }
  column "mfa_secret" {
    null = true
    type = character_varying(255)
  }
  column "created_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null    = true
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  index "idx_admin_email" {
    columns = [column.username]
  }
  index "idx_admin_username" {
    columns = [column.username]
  }
  unique "admin_account_email_key" {
    columns = [column.email]
  }
  unique "admin_account_username_key" {
    columns = [column.username]
  }
}
table "admin_refresh_token" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "account_id" {
    null = false
    type = bigint
  }
  column "device_id" {
    null = false
    type = character_varying(64)
  }
  column "issues_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "expires_at" {
    null = false
    type = bigint
  }
  column "revoked" {
    null    = false
    type    = boolean
    default = false
  }
  column "token" {
    null = false
    type = character_varying(64)
  }
  column "device_info" {
    null = false
    type = character_varying(255)
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "admin_refresh_token_account_id_fkey" {
    columns     = [column.account_id]
    ref_columns = [table.admin_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_admin_account_id" {
    columns = [column.account_id]
  }
  index "idx_admin_token_device_id" {
    columns = [column.token, column.device_id]
  }
  unique "admin_refresh_token_token_key" {
    columns = [column.token]
  }
}
table "admin_account_role" {
  schema = schema.public
  column "admin_account_id" {
    null = false
    type = bigint
  }
  column "admin_role_id" {
    null = false
    type = bigint
  }
  primary_key {
    columns = [column.admin_account_id, column.admin_role_id]
  }
  foreign_key "admin_account_role_admin_account_id_fkey" {
    columns     = [column.admin_account_id]
    ref_columns = [table.admin_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "admin_account_role_admin_role_id_fkey" {
    columns     = [column.admin_role_id]
    ref_columns = [table.admin_role.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_admin_account_role_account_id" {
    columns = [column.admin_account_id]
  }
}
table "admin_permission" {
  schema = schema.public
  column "id" {
    null = false
    type = integer
    identity {
      generated = ALWAYS
      start     = 1
      increment = 1
    }
  }
  column "group_id" {
    null = false
    type = integer
  }
  column "name" {
    null = false
    type = character_varying(64)
  }
  column "description" {
    null = true
    type = character_varying(255)
  }
  column "authorities" {
    null = false
    type = jsonb
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "admin_permission_group_id_fkey" {
    columns     = [column.group_id]
    ref_columns = [table.admin_permission_group.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  unique "admin_permission_name_key" {
    columns = [column.name]
  }
}
table "admin_permission_group" {
  schema = schema.public
  column "id" {
    null = false
    type = integer
    identity {
      generated = ALWAYS
      start     = 1
      increment = 1
    }
  }
  column "name" {
    null = false
    type = character_varying(255)
  }
  column "description" {
    null = true
    type = character_varying(255)
  }
  primary_key {
    columns = [column.id]
  }
}
table "admin_permission_role" {
  schema = schema.public
  column "admin_role_id" {
    null = false
    type = bigint
  }
  column "admin_permission_id" {
    null = false
    type = integer
  }
  primary_key {
    columns = [column.admin_role_id, column.admin_permission_id]
  }
  foreign_key "admin_permission_role_admin_permission_id_fkey" {
    columns     = [column.admin_permission_id]
    ref_columns = [table.admin_permission.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "admin_permission_role_admin_role_id_fkey" {
    columns     = [column.admin_role_id]
    ref_columns = [table.admin_role.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_admin_permission_id" {
    columns = [column.admin_permission_id]
  }
  index "idx_admin_role_id" {
    columns = [column.admin_role_id]
  }
}
table "admin_role" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "name" {
    null = false
    type = character_varying(255)
  }
  column "active" {
    null    = false
    type    = boolean
    default = true
  }
  column "description" {
    null = true
    type = character_varying(255)
  }
  column "updated_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
}
table "user_game" {
  schema = schema.public
  column "user_account_id" {
    null = false
    type = bigint
  }
  column "game_id" {
    null = false
    type = bigint
  }
  column "owned_date" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "play_seconds" {
    null = false
    type = bigint
    default = 0
  }
  column "play_recent_date" {
    null = true
    type = bigint
  }
  primary_key {
    columns = [column.user_account_id, column.game_id]
  }
  foreign_key "user_game_game_id_fkey" {
    columns     = [column.game_id]
    ref_columns = [table.game.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "user_game_user_account_id_fkey" {
    columns     = [column.user_account_id]
    ref_columns = [table.user_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_user_game_game_id" {
    columns = [column.game_id]
  }
  index "idx_user_game_user_account_id" {
    columns = [column.user_account_id]
  }
}
table "cart" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "user_account_id" {
    null = true
    type = bigint
  }
  column "created_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "cart_user_account_id_fkey" {
    columns     = [column.user_account_id]
    ref_columns = [table.user_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_cart_user_account_id" {
    columns = [column.user_account_id]
    unique  = true
  }
}
table "cart_item" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "cart_id" {
    null = false
    type = bigint
  }
  column "game_id" {
    null = false
    type = bigint
  }
  column "added_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "cart_item_cart_id_fkey" {
    columns     = [column.cart_id]
    ref_columns = [table.cart.column.id]
    on_update   = NO_ACTION
    on_delete   = CASCADE
  }
  foreign_key "cart_item_game_id_fkey" {
    columns     = [column.game_id]
    ref_columns = [table.game.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_cart_item_cart_id" {
    columns = [column.cart_id]
  }
  index "unque_cart_item_game_id" {
    columns = [column.cart_id, column.game_id]
    unique  = true
  }
}
table "orders" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "user_account_id" {
    null = false
    type = bigint
  }
  column "status" {
    null = false
    type = smallint
  }
  column "message" {
    null = true
    type = character_varying(255)
  }
  column "created_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  column "updated_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "orders_user_account_id_fkey" {
    columns     = [column.user_account_id]
    ref_columns = [table.user_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_order_user_account_id" {
    columns = [column.user_account_id]
  }
}
table "order_details" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "order_id" {
    null = false
    type = bigint
  }
  column "game_id" {
    null = false
    type = bigint
  }
  column "price" {
    null = false
    type = numeric(13, 2)
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "order_details_order_id_fkey" {
    columns     = [column.order_id]
    ref_columns = [table.orders.column.id]
    on_update   = NO_ACTION
    on_delete   = CASCADE
  }
  foreign_key "order_details_game_id_fkey" {
    columns     = [column.game_id]
    ref_columns = [table.game.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_order_details_order_id" {
    columns = [column.order_id]
  }
}
table "wishlist" {
  schema = schema.public
  column "id" {
    null = false
    type = bigint
  }
  column "user_account_id" {
    null = false
    type = bigint
  }
  column "game_id" {
    null = false
    type = bigint
  }
  column "added_at" {
    null    = false
    type    = bigint
    default = sql("(extract(epoch from (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh')) * 1000)::bigint")
  }
  primary_key {
    columns = [column.id]
  }
  foreign_key "wishlist_user_account_id_fkey" {
    columns     = [column.user_account_id]
    ref_columns = [table.user_account.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  foreign_key "wishlist_game_id_fkey" {
    columns     = [column.game_id]
    ref_columns = [table.game.column.id]
    on_update   = NO_ACTION
    on_delete   = NO_ACTION
  }
  index "idx_unique_wishlist" {
    columns = [column.user_account_id, column.game_id]
    unique  = true
  }
  index "idx_wishlist_user_account_id" {
    columns = [column.user_account_id]
  }
}
