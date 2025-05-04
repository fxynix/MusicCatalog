import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, Select, Tag, message } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Column } = Table;
const { Option } = Select;

const TrackList = () => {
  const [tracks, setTracks] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [genres, setGenres] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingTrack, setEditingTrack] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchTracks();
    fetchAlbums();
    fetchGenres();
  }, []);

  const fetchTracks = async () => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/tracks/all`);
      setTracks(response.data);
    } catch (error) {
      message.error('Failed to fetch tracks');
    }
  };

  const fetchAlbums = async () => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/albums/all`);
      setAlbums(response.data);
    } catch (error) {
      message.error('Failed to fetch albums');
    }
  };

  const fetchGenres = async () => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/genres/all`);
      setGenres(response.data);
    } catch (error) {
      message.error('Failed to fetch genres');
    }
  };

  const showModal = (track = null) => {
    setEditingTrack(track);
    form.resetFields();
    if (track) {
      form.setFieldsValue({
        name: track.name,
        duration: track.duration,
        albumId: albums.find(a => a.name === track.albumName)?.id,
        genreIds: genres
            .filter(genre => track.genres?.includes(genre.name))
            .map(genre => genre.id) || []
      });
    } else {
      form.setFieldsValue({
        name: '',
        duration: undefined,
        albumId: undefined,
        genreIds: []
      });
    }
    setIsModalVisible(true);
  };

  const handleSubmit = async (values) => {
    try {
      const requestData = {
        name: values.name,
        duration: values.duration,
        albumId: values.albumId,
        genresIds: editingTrack ? values.genreIds : undefined
      };

      if (editingTrack) {
        await axios.patch(
            `${process.env.REACT_APP_API_URL}/tracks/${editingTrack.id}`,
            requestData
        );
        message.success('Track updated successfully');
      } else {
        await axios.post(
            `${process.env.REACT_APP_API_URL}/tracks`,
            requestData
        );
        message.success('Track created successfully');
      }

      fetchTracks();
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
        message.error(error.response?.data?.message || 'Failed to save track');
      }
    }
  };

  const handleDelete = async (id) => {
    Modal.confirm({
      title: 'Delete Track',
      content: 'Are you sure you want to delete this track?',
      okText: 'Delete',
      okType: 'danger',
      cancelText: 'Cancel',
      onOk: async () => {
        try {
          await axios.delete(`${process.env.REACT_APP_API_URL}/tracks/${id}`);
          message.success('Track deleted successfully');
          fetchTracks();
        } catch (error) {
          message.error('Failed to delete track');
        }
      }
    });
  };

  return (
      <div className="container">
        <div className="actions">
          <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>
            Add Track
          </Button>
        </div>

        <Table dataSource={tracks} rowKey="id">
          <Column title="Name" dataIndex="name" key="name" />
          <Column
              title="Duration"
              key="duration"
              render={(_, track) => `${track.duration} sec`}
          />
          <Column
              title="Album"
              key="album"
              render={(_, track) => track.albumName || '-'}
          />
          <Column
              title="Artists"
              key="artists"
              render={(_, track) => (
                  track.artists?.length > 0 ? (
                      track.artists.map((artist, index) => (
                          <Tag key={index}>{artist}</Tag>
                      ))
                  ) : '-'
              )}
          />
          <Column
              title="Genres"
              key="genres"
              render={(_, track) => (
                  track.genres?.length > 0 ? (
                      track.genres.map((genre, index) => (
                          <Tag key={index}>{genre}</Tag>
                      ))
                  ) : '-'
              )}
          />
          <Column
              title="Action"
              key="action"
              render={(_, track) => (
                  <Space size="middle">
                    <Button
                        type="link"
                        icon={<EditOutlined />}
                        onClick={() => showModal(track)}
                    />
                    <Button
                        type="link"
                        icon={<DeleteOutlined />}
                        onClick={() => handleDelete(track.id)}
                        danger
                    />
                  </Space>
              )}
          />
        </Table>

        <Modal
            title={editingTrack ? "Edit Track" : "Add Track"}
            open={isModalVisible}
            onOk={() => form.submit()}
            onCancel={() => setIsModalVisible(false)}
            width={800}
        >
          <Form form={form} onFinish={handleSubmit} layout="vertical">
            <Form.Item
                name="name"
                label="Track Name"
                required={true}
            >
              <Input placeholder="Enter track name" />
            </Form.Item>

            <Form.Item
                name="duration"
                required={true}
                label="Duration (seconds)"
            >
              <Input type="number" placeholder="Enter track duration" />
            </Form.Item>

            <Form.Item
                name="albumId"
                label="Album"
                rules={[
                  { required: !editingTrack, message: 'Please select album!' }
                ]}
            >
              <Select
                  showSearch
                  optionFilterProp="children"
                  placeholder="Select album"
              >
                {albums.map(album => (
                    <Option key={album.id} value={album.id}>
                      {album.name}
                    </Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
                name="genreIds"
                label="Genres"
            >
              <Select
                  mode="multiple"
                  showSearch
                  optionFilterProp="children"
                  placeholder="Select genres"
              >
                {genres.map(genre => (
                    <Option key={genre.id} value={genre.id}>
                      {genre.name}
                    </Option>
                ))}
              </Select>
            </Form.Item>
          </Form>
        </Modal>
      </div>
  );
};

export default TrackList;