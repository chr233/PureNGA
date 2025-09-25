package com.chrxw.purenga.utils.data

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("url") val url: String?,
    @SerializedName("id") val id: Long?,
    @SerializedName("node_id") val nodeId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("label") val label: String?,
    @SerializedName("content_type") val contentType: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("size") val size: Long?,
    @SerializedName("digest") val digest: String,
    @SerializedName("download_count") val downloadCount: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("browser_download_url") val browserDownloadUrl: String?
)

