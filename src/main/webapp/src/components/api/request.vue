<template>
  <div>
    <el-button icon="el-icon-d-arrow-left" type="info" plain @click="$router.go(-1)" size="small">返回</el-button>
    <h2>接口请求测试</h2>
    <h4>接口地址：</h4>
    <div class="path">http://{{ address }}/api/{{ path }}</div>

    <h4>接口参数：</h4>
    <el-form label-width="100px" style="width: 400px" size="medium">
      <el-form-item :label="item.name + '：'" v-for="item in params">
        <el-input v-model="item.value">
          <template slot="append">{{item.type}}</template>
        </el-input>
      </el-form-item>

    </el-form>
    <el-button @click="request">发送请求</el-button>

    <h4>返回结果：</h4>

    <el-table :data="tableData" v-show="showTable" size="mini" border stripe max-height="700" >
      <el-table-column :prop="item" :label="item" v-for="item in keys" :key="item"></el-table-column>
    </el-table>
    <el-input type="textarea" v-model="response" :autosize="{ minRows: 5, maxRows: 20 }" class="my" v-show="!showTable"></el-input>

    <el-button size="small" @click="format" class="button">格式化json</el-button>
    <el-button size="small" @click="tableShow" class="button" v-if="isSelect == 1">表格展示</el-button>
    <el-button size="small" @click="tableHide" class="button" v-if="isSelect == 1">原始数据</el-button>
    <!--    <h4>请求示例：</h4>-->
    <!--    <el-collapse accordion>-->
    <!--      <el-collapse-item title="shell" name="1">-->

    <!--      </el-collapse-item>-->
    <!--      <el-collapse-item title="python" name="2">-->
    <!--        <codemirror-->
    <!--            ref="mycode"-->
    <!--            :value="curCode"-->
    <!--            :options="cmOptions"-->
    <!--            class="code"-->

    <!--        ></codemirror>-->
    <!--      </el-collapse-item>-->
    <!--      <el-collapse-item title="java" name="3">-->
    <!--        <div>简化流程：设计简洁直观的操作流程；</div>-->
    <!--        <div>清晰明确：语言表达清晰且表意明确，让用户快速理解进而作出决策；</div>-->
    <!--        <div>帮助用户识别：界面简单直白，让用户快速识别而非回忆，减少用户记忆负担。</div>-->
    <!--      </el-collapse-item>-->
    <!--      <el-collapse-item title="javascript" name="4">-->
    <!--        <div>用户决策：根据场景可给予用户操作建议或安全提示，但不能代替用户进行决策；</div>-->
    <!--        <div>结果可控：用户可以自由的进行操作，包括撤销、回退和终止当前操作等。</div>-->
    <!--      </el-collapse-item>-->
    <!--    </el-collapse>-->
  </div>
</template>

<script>
import {codemirror} from 'vue-codemirror';
import 'codemirror/theme/ambiance.css'; // 这里引入的是主题样式，根据设置的theme的主题引入，一定要引入！！
require('codemirror/mode/javascript/javascript'); // 这里引入的模式的js，根据设置的mode引入，一定要引入！！

export default {
  name: "request",
  data() {
    return {
      api: {},
      params: [],
      path: null,
      address: null,
      response: null,
      isSelect: null,

      // curCode: 'import aa',
      // cmOptions: {
      //   value: 'aaaa',
      //   mode: 'text/javascript',
      //   theme: 'ambiance',
      //   readOnly: false
      // },
      keys: [],
      tableData: [],
      showTable: false
    }
  },
  methods: {
    getDetail(id) {
      this.axios.post("/apiConfig/detail/" + id).then((response) => {
        this.path = response.data.path
        this.params = JSON.parse(response.data.params)
        this.isSelect = response.data.isSelect
      }).catch((error) => {
        this.$message.error("失败")
      })
    },
    getAddress() {
      this.axios.post("/apiConfig/getIPPort").then((response) => {
        this.address = response.data
      }).catch((error) => {
        this.$message.error("失败")
      })
    },
    request() {
      let p = {}
      this.params.forEach(t => {
        p[t.name] = t.value
      })
      let url = `http://${this.address}/api/${this.path}`
      this.axios.post("/apiConfig/request",
          {url: url, "params": JSON.stringify(p)}
      ).then((response) => {
        this.showTable = false
        if (response.data.success) {
          this.response = response.data.data

        }
      }).catch((error) => {
        this.$message.error("失败")
      })
    },
    format() {
      const o = JSON.parse(this.response)
      this.response = JSON.stringify(o, null, 4)
    },
    tableShow() {
      if (this.response == null)
        return
      let obj = JSON.parse(this.response);
      if (obj.success) {
        this.tableData = obj.data
        if (obj.data.length > 0) {
          this.keys = (Object.keys(obj.data[0]))
        } else {
          return
        }
      } else {
        return
      }
      this.showTable = true
    },
    tableHide() {
      this.showTable = false
    }
  },
  components: {codemirror},
  created() {
    this.getDetail(this.$route.query.id)
    this.getAddress()
  }

}
</script>

<style scoped>
.my >>> .el-textarea__inner {
  font-family: 'Consolas', Helvetica, Arial, sans-serif;
  /*font-size: 18px;*/
}

h2 {
  margin-bottom: 25px;
  text-align: center;
}

h4 {
  margin: 10px 0;
}

.path {
  font-size: 18px;
  border: 1px solid;
  padding: 5px;

}

.button {
  margin: 10px 10px 10px 0;
}
</style>
