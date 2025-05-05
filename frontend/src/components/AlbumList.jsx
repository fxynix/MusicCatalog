import React, { useState, useEffect } from 'react';
import {Table, Button, Space, Modal, Form, Input, Select, Tag, message, Spin} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Column } = Table;
const { Option } = Select;

const AlbumList = () => {
    const [albums, setAlbums] = useState([]);
    const [artists, setArtists] = useState([]);
    const [tracks, setTracks] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingAlbum, setEditingAlbum] = useState(null);
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchAlbums();
        fetchArtists();
        fetchTracks();
    }, []);

    const fetchAlbums = async () => {
        setLoading(true);
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_URL}/albums/all`);
            setAlbums(response.data);
        } catch (error) {
            message.error('Failed to fetch albums');
        } finally {
            setLoading(false);
        }
    };

    const fetchArtists = async () => {
        setLoading(true);
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_URL}/artists/all`);
            setArtists(response.data);
        } catch (error) {
            message.error('Failed to fetch artists');
        } finally {
            setLoading(false);
        }
    };

    const fetchTracks = async () => {
        setLoading(true);
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_URL}/tracks/all`);
            setTracks(response.data);
        } catch (error) {
            message.error('Failed to fetch tracks');
        } finally {
            setLoading(false);
        }
    };

    const showModal = (album = null) => {
        setEditingAlbum(album);
        form.resetFields();
        if (album) {
            const artistIds = artists
                .filter(artist => album.artists.includes(artist.name))
                .map(artist => artist.id);

            const trackIds = tracks
                .filter(track => album.tracks?.includes(track.name))
                .map(track => track.id);

            form.setFieldsValue({
                name: album.name,
                artistIds,
                trackIds: trackIds || []
            });
        } else {
            form.setFieldsValue({
                name: '',
                artistIds: [],
                trackIds: []
            });
        }
        setIsModalVisible(true);
    };

    const handleSubmit = async (values) => {
        try {
            const requestData = {
                name: values.name,
                artistsIds: values.artistIds,
                tracksIds: values.trackIds || [] };

            if (editingAlbum) {
                await axios.patch(
                    `${process.env.REACT_APP_API_URL}/albums/${editingAlbum.id}`,
                    requestData
                );
                message.success('Album updated successfully');
            } else {
                await axios.post(
                    `${process.env.REACT_APP_API_URL}/albums`,
                    requestData
                );
                message.success('Album created successfully');
            }

            fetchAlbums();
            setIsModalVisible(false);
        } catch (error) {
            if (error.response?.status === 400) {
                const errorMessages = Object.entries(error.response.data)
                    .flatMap(([field, errors]) =>
                        Array.isArray(errors)
                            ? errors.map(e => `${field}: ${e}`)
                            : `${field}: ${errors}`
                    )
                    .join('\n');

                message.error({
                    content: <div style={{ whiteSpace: 'pre-line' }}>{errorMessages}</div>,
                    duration: 5
                });
            } else {
                message.error(error.response?.data?.message || 'Failed to save album');
            }
        }
    };

    const handleDelete = async (id) => {
        Modal.confirm({
            title: 'Delete Album',
            content: 'Are you sure you want to delete this album?',
            okText: 'Delete',
            okType: 'danger',
            cancelText: 'Cancel',
            onOk: async () => {
                try {
                    await axios.delete(`${process.env.REACT_APP_API_URL}/albums/${id}`);
                    message.success('Album deleted successfully');
                    fetchAlbums();
                } catch (error) {
                    message.error('Failed to delete album');
                }
            }
        });
    };

    return (
        <Spin spinning={loading} tip="Loading...">
        <div className="container">
            <div className="actions">
                <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>
                    Add Album
                </Button>
            </div>

            <Table dataSource={albums} rowKey="id">
                <Column title="Name" dataIndex="name" key="name" />
                <Column
                    title="Artists"
                    key="artists"
                    render={(_, album) => (
                        album.artists?.length > 0 ? (
                            album.artists.map((artist, index) => (
                                <Tag key={index}>{artist}</Tag>
                            ))
                        ) : '-'
                    )}
                />
                <Column
                    title="Tracks"
                    key="tracks"
                    render={(_, album) => album.tracks?.length || 0}
                />
                <Column
                    title="Action"
                    key="action"
                    render={(_, album) => (
                        <Space size="middle">
                            <Button
                                type="link"
                                icon={<EditOutlined />}
                                onClick={() => showModal(album)}
                            />
                            <Button
                                type="link"
                                icon={<DeleteOutlined />}
                                onClick={() => handleDelete(album.id)}
                                danger
                            />
                        </Space>
                    )}
                />
            </Table>

            <Modal
                title={editingAlbum ? "Edit Album" : "Add Album"}
                open={isModalVisible}
                onOk={() => form.submit()}
                onCancel={() => setIsModalVisible(false)}
                width={800}
            >
                <Form form={form} onFinish={handleSubmit} layout="vertical">
                    <Form.Item
                        name="name"
                        label="Album Name"
                        required={true}

                    >
                        <Input placeholder="Enter album name" />
                    </Form.Item>

                    <Form.Item
                        name="artistIds"
                        label="Artists"
                        rules={[
                            { required: true, message: 'Please select at least one artist!' }
                        ]}
                    >
                        <Select
                            mode="multiple"
                            showSearch
                            optionFilterProp="children"
                            placeholder="Select artists"
                            allowClear={false}
                        >
                            {artists.map(artist => (
                                <Option key={artist.id} value={artist.id}>
                                    {artist.name}
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>

                    {editingAlbum && (
                        <Form.Item
                            name="trackIds"
                            label="Tracks"
                            initialValue={[]}
                        >
                            <Select
                                mode="multiple"
                                showSearch
                                optionFilterProp="children"
                                placeholder="Select tracks"
                            >
                                {tracks.map(track => (
                                    <Option key={track.id} value={track.id}>
                                        {track.name}
                                    </Option>
                                ))}
                            </Select>
                        </Form.Item>
                    )}
                </Form>
            </Modal>
        </div>
        </Spin>
    );
};

export default AlbumList;