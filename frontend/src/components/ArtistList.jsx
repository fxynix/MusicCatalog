import React, { useState, useEffect } from 'react';
import {Table, Button, Space, Modal, Form, Input, Select, message, Spin} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Column } = Table;
const { Option } = Select;

const ArtistList = () => {
  const [artists, setArtists] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingArtist, setEditingArtist] = useState(null);
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchArtists();
    fetchAlbums();
  }, []);

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

  const showModal = (artist = null) => {
    setEditingArtist(artist);
    form.resetFields();
    if (artist) {
      form.setFieldsValue({
        name: artist.name,
        albumsIds: albums
            .filter(album => artist.albums?.includes(album.name))
            .map(album => album.id) || []
      });
    } else {
      form.setFieldsValue({
        name: '',
        albumsIds: []
      });
    }
    setIsModalVisible(true);
  };

  const handleSubmit = async (values) => {
    try {
      const requestData = {
        name: values.name,
        ...(editingArtist && { albumsIds: values.albumsIds || [] })
      };

      if (editingArtist) {
        await axios.patch(
            `${process.env.REACT_APP_API_URL}/artists/${editingArtist.id}`,
            requestData
        );
        message.success('Artist updated successfully');
      } else {
        await axios.post(
            `${process.env.REACT_APP_API_URL}/artists`,
            { name: values.name }
        );
        message.success('Artist created successfully');
      }

      fetchArtists();
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
        message.error(error.response?.data?.message || 'Failed to save artist');
      }
    }
  };

  const handleDelete = async (id) => {
    Modal.confirm({
      title: 'Delete Artist',
      content: 'Are you sure you want to delete this artist?',
      okText: 'Delete',
      okType: 'danger',
      cancelText: 'Cancel',
      onOk: async () => {
        try {
          await axios.delete(`${process.env.REACT_APP_API_URL}/artists/${id}`);
          message.success('Artist deleted successfully');
          fetchArtists();
        } catch (error) {
          message.error(error.response?.data?.message || 'Failed to delete artist');
        }
      }
    });
  };

  return (
      <Spin spinning={loading} tip="Loading...">
      <div className="container">
        <div className="actions">
          <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>
            Add Artist
          </Button>
        </div>

        <Table dataSource={artists} rowKey="id">
          <Column title="Name" dataIndex="name" key="name" />
          <Column
              title="Albums"
              key="albums"
              render={(_, artist) => artist.albums?.length || 0}
          />
          <Column
              title="Action"
              key="action"
              render={(_, artist) => (
                  <Space size="middle">
                    <Button
                        type="link"
                        icon={<EditOutlined />}
                        onClick={() => showModal(artist)}
                    />
                    <Button
                        type="link"
                        icon={<DeleteOutlined />}
                        onClick={() => handleDelete(artist.id)}
                        danger
                    />
                  </Space>
              )}
          />
        </Table>

        <Modal
            title={editingArtist ? "Edit Artist" : "Add Artist"}
            open={isModalVisible}
            onOk={() => form.submit()}
            onCancel={() => setIsModalVisible(false)}
            width={700}
        >
          <Form form={form} onFinish={handleSubmit} layout="vertical">
            <Form.Item
                name="name"
                label="Artist Name"
                required={true}

            >
              <Input placeholder="Enter artist name" />
            </Form.Item>

            {editingArtist && (
                <Form.Item
                    name="albumsIds"
                    label="Albums"
                >
                  <Select
                      mode="multiple"
                      showSearch
                      optionFilterProp="children"
                      placeholder="Select albums"
                      allowClear
                  >
                    {albums.map(album => (
                        <Option key={album.id} value={album.id}>
                          {album.name}
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

export default ArtistList;