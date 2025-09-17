package com.chrxw.purenga.utils.data

import com.google.gson.annotations.SerializedName

data class Release(
    @SerializedName("url") val url: String,
    @SerializedName("assets_url") val assetsUrl: String,
    @SerializedName("upload_url") val uploadUrl: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("id") val id: Long,
    @SerializedName("author") val author: Author,
    @SerializedName("node_id") val nodeId: String,
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("target_commitish") val targetCommitish: String,
    @SerializedName("name") val name: String,
    @SerializedName("draft") val draft: Boolean,
    @SerializedName("immutable") val immutable: Boolean,
    @SerializedName("prerelease") val prerelease: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("assets") val assets: List<Asset>,
    @SerializedName("tarball_url") val tarballUrl: String,
    @SerializedName("zipball_url") val zipballUrl: String,
    @SerializedName("body") val body: String,
)