/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import * as React from 'react';
import RestartForm from '../../../components/common/RestartForm';
import { EditionStatus } from '../../../api/marketplace';
import { translate } from '../../../helpers/l10n';

interface Props {
  editionStatus: EditionStatus;
}

interface State {
  openRestart: boolean;
}

export default class EditionsStatusNotif extends React.PureComponent<Props, State> {
  state: State = { openRestart: false };

  handleOpenRestart = () => this.setState({ openRestart: true });
  hanleCloseRestart = () => this.setState({ openRestart: false });

  render() {
    const { editionStatus } = this.props;
    if (editionStatus.installationStatus === 'AUTOMATIC_IN_PROGRESS') {
      return (
        <div className="alert alert-page alert-info">
          <i className="spinner spacer-right text-bottom" />
          <span>{translate('marketplace.status.AUTOMATIC_IN_PROGRESS')}</span>
        </div>
      );
    } else if (editionStatus.installationStatus === 'AUTOMATIC_READY') {
      return (
        <div className="alert alert-page alert-success">
          <span>{translate('marketplace.status.AUTOMATIC_READY')}</span>
          <button className="js-restart spacer-left" onClick={this.handleOpenRestart}>
            {translate('marketplace.restart')}
          </button>
          {this.state.openRestart && <RestartForm onClose={this.hanleCloseRestart} />}
        </div>
      );
    } else if (
      ['MANUAL_IN_PROGRESS', 'AUTOMATIC_FAILURE'].includes(editionStatus.installationStatus)
    ) {
      return (
        <div className="alert alert-page alert-danger">
          {translate('marketplace.status', editionStatus.installationStatus)}
          <a className="little-spacer-left" href="https://www.sonarsource.com" target="_blank">
            {translate('marketplace.how_to_install')}
          </a>
        </div>
      );
    }
    return null;
  }
}