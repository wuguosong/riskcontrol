package com.yk.sign.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2019/4/29 0029.
 */
public class _ApprovalNode {
    public static final String _approvalStateDo = "nodeItem";// 未处理
    public static final String _approvalStateDoing = "nodeItem activeNode activingNode";// 在处理
    public static final String _approvalStateDone = "nodeItem activeNode";// 已处理
    public static final String _approvalKey = "_approvalKey";
    public static final String _approvalDate = "_approvalDate";
    public static final String _approvalUser = "_approvalUser";
    public static final String _approvalState = "_approvalState";
    public static final String _approvalStateCode = "_approvalStateCode";
    private String _processKey;
    private String _processId;
    private _FormalApproval _formalApproval;
    private _ReviewApproval _reviewApproval;
    private _BulletinApproval _bulletinApproval;

    public _FormalApproval get_formalApproval() {
        return _formalApproval;
    }


    public _ReviewApproval get_reviewApproval() {
        return _reviewApproval;
    }


    public _BulletinApproval get_bulletinApproval() {
        return _bulletinApproval;
    }

    public void set_formalApproval(_FormalApproval _formalApproval) {
        this._formalApproval = _formalApproval;
    }

    public void set_reviewApproval(_ReviewApproval _reviewApproval) {
        this._reviewApproval = _reviewApproval;
    }

    public void set_bulletinApproval(_BulletinApproval _bulletinApproval) {
        this._bulletinApproval = _bulletinApproval;
    }

    public static class _FormalApproval implements _Approval{
        private JSONObject _drafting;// 创建人提交
        private JSONObject _investmentManagerDrafting;// 投资经理起草
        private JSONObject _businessAreaApproval;// 业务区审批
        private JSONObject _waterInvestmentCenter;// 投资中心/水环境
        private JSONObject _assignmentTask;// 分配评审任务
        private JSONObject _legalDistribution;// 法律分配
        private JSONObject _lawChargeApproval;// 法律负责人审批
        private JSONObject _reviewChargeApproval;// 评审负责人审批
        private JSONObject _reviewChargeConfirm;// 评审负责人确认
        private JSONObject _completed;// 完成

        public JSONObject get_drafting() {
            if(_drafting == null){
                _drafting = new JSONObject();
            }
            _drafting.put(_ApprovalNode._approvalKey, "创建人提交");
            return _drafting;
        }

        public void set_drafting(JSONObject _drafting) {
            this._drafting = _drafting;
        }

        public JSONObject get_investmentManagerDrafting() {
            if(_investmentManagerDrafting == null){
                _investmentManagerDrafting = new JSONObject();
            }
            _investmentManagerDrafting.put(_ApprovalNode._approvalKey, "投资经理起草");
            return _investmentManagerDrafting;
        }

        public void set_investmentManagerDrafting(JSONObject _investmentManagerDrafting) {
            this._investmentManagerDrafting = _investmentManagerDrafting;
        }

        public JSONObject get_businessAreaApproval() {
            if(_businessAreaApproval == null){
                _businessAreaApproval = new JSONObject();
            }
            _businessAreaApproval.put(_ApprovalNode._approvalKey, "大区审批,大区领导审批");
            return _businessAreaApproval;
        }

        public void set_businessAreaApproval(JSONObject _businessAreaApproval) {
            this._businessAreaApproval = _businessAreaApproval;
        }

        public JSONObject get_waterInvestmentCenter() {
            if(_waterInvestmentCenter == null){
                _waterInvestmentCenter = new JSONObject();
            }
            _waterInvestmentCenter.put(_ApprovalNode._approvalKey, "投资中心/水环境,投资中心/水环境审批");
            return _waterInvestmentCenter;
        }

        public void set_waterInvestmentCenter(JSONObject _waterInvestmentCenter) {
            this._waterInvestmentCenter = _waterInvestmentCenter;
        }

        public JSONObject get_legalDistribution() {
            if(_legalDistribution == null){
                _legalDistribution = new JSONObject();
            }
            _legalDistribution.put(_ApprovalNode._approvalKey, "法律分配");
            return _legalDistribution;
        }

        public void set_legalDistribution(JSONObject _legalDistribution) {
            this._legalDistribution = _legalDistribution;
        }

        public JSONObject get_assignmentTask() {
            if(_assignmentTask == null){
                _assignmentTask = new JSONObject();
            }
            _assignmentTask.put(_ApprovalNode._approvalKey, "分配评审任务,风控负责人分配任务");
            return _assignmentTask;
        }

        public void set_assignmentTask(JSONObject _assignmentTask) {
            this._assignmentTask = _assignmentTask;
        }

        public JSONObject get_lawChargeApproval() {
            if(_lawChargeApproval == null){
                _lawChargeApproval = new JSONObject();
            }
            _lawChargeApproval.put(_ApprovalNode._approvalKey, "法律负责人审批");
            return _lawChargeApproval;
        }

        public void set_lawChargeApproval(JSONObject _lawChargeApproval) {
            this._lawChargeApproval = _lawChargeApproval;
        }

        public JSONObject get_reviewChargeApproval() {
            if(_reviewChargeApproval == null){
                _reviewChargeApproval = new JSONObject();
            }
            _reviewChargeApproval.put(_ApprovalNode._approvalKey, "评审负责人审批");
            return _reviewChargeApproval;
        }

        public void set_reviewChargeApproval(JSONObject _reviewChargeApproval) {
            this._reviewChargeApproval = _reviewChargeApproval;
        }

        public JSONObject get_reviewChargeConfirm() {
            if(_reviewChargeConfirm == null){
                _reviewChargeConfirm = new JSONObject();
            }
            _reviewChargeConfirm.put(_ApprovalNode._approvalKey, "评审负责人确认");
            return _reviewChargeConfirm;
        }

        public void set_reviewChargeConfirm(JSONObject _reviewChargeConfirm) {
            this._reviewChargeConfirm = _reviewChargeConfirm;
        }

        public JSONObject get_completed() {
            if(_completed == null){
                _completed = new JSONObject();
            }
            _completed.put(_ApprovalNode._approvalKey, "通过结束");
            return _completed;
        }

        public void set_completed(JSONObject _completed) {
            this._completed = _completed;
        }

        @Override
        public void execute() {
            if(this.get_reviewChargeConfirm().getInteger(_ApprovalNode._approvalStateCode) == 1){// 审批中
                this.get_completed().put(_ApprovalNode._approvalStateCode, 0);
                this.get_completed().put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDo);
            }else if(this.get_reviewChargeConfirm().getInteger(_ApprovalNode._approvalStateCode) == -1){// 已审批
                this.get_completed().put(_ApprovalNode._approvalStateCode, -1);
                this.get_completed().put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDone);
            }
            //this.get_investmentManagerDrafting().put(_ApprovalNode._approvalState, this.get_drafting().get(_ApprovalNode._approvalState));
            //this.get_investmentManagerDrafting().put(_ApprovalNode._approvalStateCode, this.get_drafting().get(_ApprovalNode._approvalStateCode));
        }
    }

    public static class _ReviewApproval implements _Approval{
        /*private JSONObject _drafting;// 创建人提交
        private JSONObject _investmentManagerDrafting;// 投资经理起草
        private JSONObject _businessAreaApproval;// 大区审批
        private JSONObject _waterInvestmentCenter;// 投资中心/水环境
        private JSONObject _assignmentTask;// 分配任务
        private JSONObject _reviewChargeApproval;// 评审负责人审批
        private JSONObject _reviewChargeConfirm;// 评审负责人确认
        private JSONObject _completed;// 完成
        public JSONObject get_drafting() {
            if(_drafting == null){
                _drafting = new JSONObject();
            }
            _drafting.put(_ApprovalNode._approvalKey, "创建人提交");
            return _drafting;
        }

        public void set_drafting(JSONObject _drafting) {
            this._drafting = _drafting;
        }

        public JSONObject get_investmentManagerDrafting() {
            if(_investmentManagerDrafting == null){
                _investmentManagerDrafting = new JSONObject();
            }
            _investmentManagerDrafting.put(_ApprovalNode._approvalKey, "投资经理起草");
            return _investmentManagerDrafting;
        }

        public void set_investmentManagerDrafting(JSONObject _investmentManagerDrafting) {
            this._investmentManagerDrafting = _investmentManagerDrafting;
        }

        public JSONObject get_businessAreaApproval() {
            if(_businessAreaApproval == null){
                _businessAreaApproval = new JSONObject();
            }
            _businessAreaApproval.put(_ApprovalNode._approvalKey, "大区审批,大区领导审批");
            return _businessAreaApproval;
        }

        public void set_businessAreaApproval(JSONObject _businessAreaApproval) {
            this._businessAreaApproval = _businessAreaApproval;
        }

        public JSONObject get_waterInvestmentCenter() {
            if(_waterInvestmentCenter == null){
                _waterInvestmentCenter = new JSONObject();
            }
            _waterInvestmentCenter.put(_ApprovalNode._approvalKey, "投资中心/水环境,投资中心/水环境审批");
            return _waterInvestmentCenter;
        }

        public void set_waterInvestmentCenter(JSONObject _waterInvestmentCenter) {
            this._waterInvestmentCenter = _waterInvestmentCenter;
        }

        public JSONObject get_assignmentTask() {
            if(_assignmentTask == null){
                _assignmentTask = new JSONObject();
            }
            _assignmentTask.put(_ApprovalNode._approvalKey, "分配评审任务,分配任务,风控负责人分配任务");
            return _assignmentTask;
        }

        public void set_assignmentTask(JSONObject _assignmentTask) {
            this._assignmentTask = _assignmentTask;
        }

        public JSONObject get_reviewChargeApproval() {
            if(_reviewChargeApproval == null){
                _reviewChargeApproval = new JSONObject();
            }
            _reviewChargeApproval.put(_ApprovalNode._approvalKey, "评审负责人审批,风控评审负责人审批");
            return _reviewChargeApproval;
        }

        public void set_reviewChargeApproval(JSONObject _reviewChargeApproval) {
            this._reviewChargeApproval = _reviewChargeApproval;
        }

        public JSONObject get_reviewChargeConfirm() {
            if(_reviewChargeConfirm == null){
                _reviewChargeConfirm = new JSONObject();
            }
            _reviewChargeConfirm.put(_ApprovalNode._approvalKey, "评审负责人确认,风控评审负责人确认");
            return _reviewChargeConfirm;
        }

        public void set_reviewChargeConfirm(JSONObject _reviewChargeConfirm) {
            this._reviewChargeConfirm = _reviewChargeConfirm;
        }

        public JSONObject get_completed() {
            if(_completed == null){
                _completed = new JSONObject();
            }
            _completed.put(_ApprovalNode._approvalKey, "通过结束");
            return _completed;
        }

        public void set_completed(JSONObject _completed) {
            this._completed = _completed;
        }

        @Override
        public void execute() {
        }*/
        private JSONObject _drafting;// 创建人提交
        private JSONObject _investmentManagerDrafting;// 投资经理起草
        private JSONObject _businessAreaApproval;// 业务区审批
        private JSONObject _waterInvestmentCenter;// 投资中心/水环境
        private JSONObject _assignmentTask;// 分配评审任务
        private JSONObject _legalDistribution;// 法律分配
        private JSONObject _lawChargeApproval;// 法律负责人审批
        private JSONObject _reviewChargeApproval;// 评审负责人审批
        private JSONObject _reviewChargeConfirm;// 评审负责人确认
        private JSONObject _completed;// 完成

        public JSONObject get_drafting() {
            if(_drafting == null){
                _drafting = new JSONObject();
            }
            _drafting.put(_ApprovalNode._approvalKey, "创建人提交");
            return _drafting;
        }

        public void set_drafting(JSONObject _drafting) {
            this._drafting = _drafting;
        }

        public JSONObject get_investmentManagerDrafting() {
            if(_investmentManagerDrafting == null){
                _investmentManagerDrafting = new JSONObject();
            }
            _investmentManagerDrafting.put(_ApprovalNode._approvalKey, "投资经理起草");
            return _investmentManagerDrafting;
        }

        public void set_investmentManagerDrafting(JSONObject _investmentManagerDrafting) {
            this._investmentManagerDrafting = _investmentManagerDrafting;
        }

        public JSONObject get_businessAreaApproval() {
            if(_businessAreaApproval == null){
                _businessAreaApproval = new JSONObject();
            }
            _businessAreaApproval.put(_ApprovalNode._approvalKey, "大区审批,大区领导审批");
            return _businessAreaApproval;
        }

        public void set_businessAreaApproval(JSONObject _businessAreaApproval) {
            this._businessAreaApproval = _businessAreaApproval;
        }

        public JSONObject get_waterInvestmentCenter() {
            if(_waterInvestmentCenter == null){
                _waterInvestmentCenter = new JSONObject();
            }
            _waterInvestmentCenter.put(_ApprovalNode._approvalKey, "投资中心/水环境,投资中心/水环境审批");
            return _waterInvestmentCenter;
        }

        public void set_waterInvestmentCenter(JSONObject _waterInvestmentCenter) {
            this._waterInvestmentCenter = _waterInvestmentCenter;
        }

        public JSONObject get_legalDistribution() {
            if(_legalDistribution == null){
                _legalDistribution = new JSONObject();
            }
            _legalDistribution.put(_ApprovalNode._approvalKey, "法律分配");
            return _legalDistribution;
        }

        public void set_legalDistribution(JSONObject _legalDistribution) {
            this._legalDistribution = _legalDistribution;
        }

        public JSONObject get_assignmentTask() {
            if(_assignmentTask == null){
                _assignmentTask = new JSONObject();
            }
            _assignmentTask.put(_ApprovalNode._approvalKey, "分配评审任务,分配任务,风控负责人分配任务");
            return _assignmentTask;
        }

        public void set_assignmentTask(JSONObject _assignmentTask) {
            this._assignmentTask = _assignmentTask;
        }

        public JSONObject get_lawChargeApproval() {
            if(_lawChargeApproval == null){
                _lawChargeApproval = new JSONObject();
            }
            _lawChargeApproval.put(_ApprovalNode._approvalKey, "法律负责人审批");
            return _lawChargeApproval;
        }

        public void set_lawChargeApproval(JSONObject _lawChargeApproval) {
            this._lawChargeApproval = _lawChargeApproval;
        }

        public JSONObject get_reviewChargeApproval() {
            if(_reviewChargeApproval == null){
                _reviewChargeApproval = new JSONObject();
            }
            _reviewChargeApproval.put(_ApprovalNode._approvalKey, "评审负责人审批,风控评审负责人审批");
            return _reviewChargeApproval;
        }

        public void set_reviewChargeApproval(JSONObject _reviewChargeApproval) {
            this._reviewChargeApproval = _reviewChargeApproval;
        }

        public JSONObject get_reviewChargeConfirm() {
            if(_reviewChargeConfirm == null){
                _reviewChargeConfirm = new JSONObject();
            }
            _reviewChargeConfirm.put(_ApprovalNode._approvalKey, "评审负责人确认,风控评审负责人确认");
            return _reviewChargeConfirm;
        }

        public void set_reviewChargeConfirm(JSONObject _reviewChargeConfirm) {
            this._reviewChargeConfirm = _reviewChargeConfirm;
        }

        public JSONObject get_completed() {
            if(_completed == null){
                _completed = new JSONObject();
            }
            _completed.put(_ApprovalNode._approvalKey, "通过结束");
            return _completed;
        }

        public void set_completed(JSONObject _completed) {
            this._completed = _completed;
        }

        @Override
        public void execute() {
            if(this.get_reviewChargeConfirm().getInteger(_ApprovalNode._approvalStateCode) == 1){// 审批中
                this.get_completed().put(_ApprovalNode._approvalStateCode, 0);
                this.get_completed().put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDo);
            }else if(this.get_reviewChargeConfirm().getInteger(_ApprovalNode._approvalStateCode) == -1){// 已审批
                this.get_completed().put(_ApprovalNode._approvalStateCode, -1);
                this.get_completed().put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDone);
            }
        }
    }

    /**
     * 其它评审
     */
    public static class _BulletinApproval implements _Approval{
        private String _backgroundFirstLine;
        private Integer _choice;
        private JSONObject _drafting;// 创建人提交
        private JSONObject _unitChargeApproval;// 单位负责人审批
        private JSONObject _businessLeaderApproval;// 业务负责人审批
        private JSONObject _assignmentTask;// 分配任务
        private JSONObject _lawChargeApproval;// 法律负责人审批
        private JSONObject _reviewChargeApproval;// 评审负责人审批
        private JSONObject _completed;// 通过结束

        public JSONObject get_drafting() {
            if(_drafting == null){
                _drafting = new JSONObject();
            }
            _drafting.put(_ApprovalNode._approvalKey, "创建人提交");
            return _drafting;
        }


        public JSONObject get_unitChargeApproval() {
            if(_unitChargeApproval == null){
                _unitChargeApproval = new JSONObject();
            }
            _unitChargeApproval.put(_ApprovalNode._approvalKey, "单位负责人审批");
            return _unitChargeApproval;
        }


        public JSONObject get_businessLeaderApproval() {
            if(_businessLeaderApproval == null){
                _businessLeaderApproval = new JSONObject();
            }
            _businessLeaderApproval.put(_ApprovalNode._approvalKey, "业务负责人审批,风控负责人审批");
            return _businessLeaderApproval;
        }

        public JSONObject get_assignmentTask() {
            if(_assignmentTask == null){
                _assignmentTask = new JSONObject();
            }
            _assignmentTask.put(_ApprovalNode._approvalKey, "分配任务");
            return _assignmentTask;
        }

        public JSONObject get_lawChargeApproval() {
            if(_lawChargeApproval == null){
                _lawChargeApproval = new JSONObject();
            }
            _lawChargeApproval.put(_ApprovalNode._approvalKey, "法律负责人审批");
            return _lawChargeApproval;
        }

        public JSONObject get_reviewChargeApproval() {
            if(_reviewChargeApproval == null){
                _reviewChargeApproval = new JSONObject();
            }
            _reviewChargeApproval.put(_ApprovalNode._approvalKey, "评审负责人审批");
            return _reviewChargeApproval;
        }

        public JSONObject get_completed() {
            if(_completed == null){
                _completed = new JSONObject();
            }
            _completed.put(_ApprovalNode._approvalKey, "通过结束");
            return _completed;
        }

        public Integer get_choice() {
            return _choice;
        }

        public void set_choice(Integer _choice) {
            this._choice = _choice;
        }

        public String get_backgroundFirstLine() {
            return _backgroundFirstLine;
        }

        public void set_backgroundFirstLine(String _backgroundFirstLine) {
            this._backgroundFirstLine = _backgroundFirstLine;
        }

        @Override
        public void execute(){
            if(this.get_lawChargeApproval().getInteger(_ApprovalNode._approvalStateCode) == 1
                    || this.get_reviewChargeApproval().getInteger(_ApprovalNode._approvalStateCode) == 1){
                this.get_completed().put(_ApprovalNode._approvalStateCode, 0);
                this.get_completed().put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDo);
            }else if(this.get_lawChargeApproval().getInteger(_ApprovalNode._approvalStateCode) == -1
                    && this.get_reviewChargeApproval().getInteger(_ApprovalNode._approvalStateCode) == -1){
                this.get_completed().put(_ApprovalNode._approvalStateCode, -1);
                this.get_completed().put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDone);
            }
            if(this.get_unitChargeApproval().getInteger(_ApprovalNode._approvalStateCode) == -1){
                this.get_drafting().put(_ApprovalNode._approvalState, this.get_unitChargeApproval().get(_ApprovalNode._approvalState));
                this.get_drafting().put(_ApprovalNode._approvalStateCode, this.get_unitChargeApproval().get(_ApprovalNode._approvalStateCode));
            }else if(this.get_unitChargeApproval().getInteger(_ApprovalNode._approvalStateCode) == 1){
                this.get_drafting().put(_ApprovalNode._approvalState, _ApprovalNode._approvalStateDone);
                this.get_drafting().put(_ApprovalNode._approvalStateCode, -1);
            }
        }

        public void set_drafting(JSONObject _drafting) {
            this._drafting = _drafting;
        }

        public void set_unitChargeApproval(JSONObject _unitChargeApproval) {
            this._unitChargeApproval = _unitChargeApproval;
        }

        public void set_businessLeaderApproval(JSONObject _businessLeaderApproval) {
            this._businessLeaderApproval = _businessLeaderApproval;
        }

        public void set_assignmentTask(JSONObject _assignmentTask) {
            this._assignmentTask = _assignmentTask;
        }

        public void set_lawChargeApproval(JSONObject _lawChargeApproval) {
            this._lawChargeApproval = _lawChargeApproval;
        }

        public void set_reviewChargeApproval(JSONObject _reviewChargeApproval) {
            this._reviewChargeApproval = _reviewChargeApproval;
        }

        public void set_completed(JSONObject _completed) {
            this._completed = _completed;
        }
    }

    public String get_processKey() {
        return _processKey;
    }

    public void set_processKey(String _processKey) {
        this._processKey = _processKey;
    }

    public String get_processId() {
        return _processId;
    }

    public void set_processId(String _processId) {
        this._processId = _processId;
    }

    public interface _Approval{
        void execute();
    }
}
