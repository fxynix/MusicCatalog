import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, message } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Column } = Table;

const GenreList = () => {
  const [genres, setGenres] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingGenre, setEditingGenre] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchGenres();
  }, []);

  const fetchGenres = async () => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/genres/all`);
      setGenres(response.data);
    } catch (error) {
      message.error('Failed to fetch genres');
    }
  };

  const showModal = (genre = null) => {
    setEditingGenre(genre);
    form.resetFields();
    if (genre) {
      form.setFieldsValue({
        name: genre.name
      });
    } else {
      form.setFieldsValue({
        name: ''
      });
    }
    setIsModalVisible(true);
  };

  const handleSubmit = async (values) => {
    try {
      if (editingGenre) {
        if (values.name !== editingGenre.name) {
          await axios.patch(
              `${process.env.REACT_APP_API_URL}/genres/${editingGenre.id}`,
              { name: values.name }
          );
          message.success('Genre updated successfully');
        } else {
          message.info('No changes detected');
          setIsModalVisible(false);
          return;
        }
      } else {
        await axios.post(
            `${process.env.REACT_APP_API_URL}/genres`,
            { name: values.name }
        );
        message.success('Genre created successfully');
      }

      fetchGenres();
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
      } else if (error.response?.status === 409) {
        const errorMessage = error.response.message;
        message.error({
          content: (
              <div style={{whiteSpace: 'pre-line'}}>
                {errorMessage}
              </div>
          ),
          duration: 5,
        });
      } else {
        message.error(error.response?.data?.message || 'Failed to save genre');
      }
    }
  };

  const handleDelete = async (id) => {
    Modal.confirm({
      title: 'Delete Genre',
      content: 'Are you sure you want to delete this genre?',
      okText: 'Delete',
      okType: 'danger',
      cancelText: 'Cancel',
      onOk: async () => {
        try {
          await axios.delete(`${process.env.REACT_APP_API_URL}/genres/${id}`);
          message.success('Genre deleted successfully');
          fetchGenres();
        } catch (error) {
          message.error(error.response?.data?.message || 'Failed to delete genre');
        }
      }
    });
  };

  return (
      <div className="container">
        <div className="actions">
          <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => showModal()}
          >
            Add Genre
          </Button>
        </div>

        <Table dataSource={genres} rowKey="id">
          <Column title="Name" dataIndex="name" key="name" />
          <Column
              title="Tracks"
              key="tracks"
              render={(_, genre) => genre.tracksCount || 0}
          />
          <Column
              title="Action"
              key="action"
              render={(_, genre) => (
                  <Space size="middle">
                    <Button
                        type="link"
                        icon={<EditOutlined />}
                        onClick={() => showModal(genre)}
                    />
                    <Button
                        type="link"
                        icon={<DeleteOutlined />}
                        onClick={() => handleDelete(genre.id)}
                        danger
                    />
                  </Space>
              )}
          />
        </Table>

        <Modal
            title={editingGenre ? "Edit Genre" : "Add Genre"}
            open={isModalVisible}
            onOk={() => form.submit()}
            onCancel={() => setIsModalVisible(false)}
        >
          <Form form={form} onFinish={handleSubmit} layout="vertical">
            <Form.Item
                name="name"
                label="Genre Name"
                required={true}

            >
              <Input placeholder="Enter genre name" />
            </Form.Item>
          </Form>
        </Modal>
      </div>
  );
};

export default GenreList;